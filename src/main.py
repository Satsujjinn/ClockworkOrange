import asyncio
import hashlib
import logging
import os
from typing import Any, AsyncGenerator, Dict, List

import asyncpg
import structlog
import ujson
from fastapi import Depends, FastAPI, Request, WebSocket, WebSocketDisconnect
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import StreamingResponse, UJSONResponse
from pydantic import BaseModel

from .audio.utils import load_audio
from .features.mfcc import extract_mfcc
from .filter.denoise import denoise
from .llm.chord_suggester import ChordSuggester
from .accompaniment.generator import AccompanimentGenerator

try:
    import aioredis
except ImportError:  # pragma: no cover - aioredis may not be installed
    aioredis = None  # type: ignore

logger = structlog.get_logger()

app = FastAPI(title="Music AI Service", default_response_class=UJSONResponse)
app.add_middleware(GZipMiddleware, minimum_size=1000)


class ChordRequest(BaseModel):
    file_path: str


class ChordResponse(BaseModel):
    chords: List[str]
    accompaniment: List[str]


@app.on_event("startup")
async def startup() -> None:
    try:
        app.state.db_pool = await asyncpg.create_pool(
            os.getenv("DATABASE_URL", "postgresql://postgres@localhost/postgres")
        )
    except Exception as exc:  # pragma: no cover - DB optional in tests
        logger.warning("db connection failed", exc_info=exc)
        app.state.db_pool = None
    if aioredis:
        try:
            app.state.redis = aioredis.from_url(
                os.getenv("REDIS_URL", "redis://localhost"), decode_responses=True
            )
        except Exception as exc:  # pragma: no cover - Redis optional in tests
            logger.warning("redis connection failed", exc_info=exc)
            app.state.redis = None


@app.on_event("shutdown")
async def shutdown() -> None:
    if app.state.db_pool:
        await app.state.db_pool.close()
    if aioredis and getattr(app.state, "redis", None):
        await app.state.redis.close()
        await app.state.redis.connection_pool.disconnect()


async def get_db(request: Request) -> asyncpg.Pool:
    return request.app.state.db_pool


async def get_redis(request: Request):
    return getattr(request.app.state, "redis", None)


class MusicService:
    def __init__(self, redis: Any) -> None:
        self.suggester = ChordSuggester()
        self.generator = AccompanimentGenerator()
        self.redis = redis

    async def __call__(self, req: ChordRequest) -> ChordResponse:
        audio_bytes = await load_audio(req.file_path)
        features = await asyncio.to_thread(extract_mfcc, audio_bytes)
        features = await asyncio.to_thread(denoise, features)
        key = "chords:" + hashlib.sha1(str(features).encode()).hexdigest()
        chords: List[str]
        if self.redis:
            cached = await self.redis.get(key)
            if cached:
                chords = ujson.loads(cached)
            else:
                chords = await asyncio.to_thread(self.suggester.suggest, features)
                await self.redis.set(key, ujson.dumps(chords))
        else:
            chords = await asyncio.to_thread(self.suggester.suggest, features)
        accompaniment = await asyncio.to_thread(self.generator.generate, chords)
        return ChordResponse(chords=chords, accompaniment=accompaniment)


async def get_service(redis=Depends(get_redis)) -> MusicService:
    return MusicService(redis)


@app.middleware("http")
async def profiling_middleware(request: Request, call_next):
    start = asyncio.get_event_loop().time()
    response = await call_next(request)
    duration = asyncio.get_event_loop().time() - start
    logger.info("request", path=request.url.path, duration=duration)
    response.headers["X-Process-Time"] = str(duration)
    return response


@app.get("/healthz")
async def healthz() -> Dict[str, str]:
    return {"status": "ok"}


@app.post("/chords", response_model=ChordResponse)
async def suggest_chords(req: ChordRequest, service: MusicService = Depends(get_service)):
    return await service(req)


@app.get("/stream")
async def stream() -> StreamingResponse:
    async def event_generator() -> AsyncGenerator[bytes, None]:
        yield b"data: ready\n\n"
    return StreamingResponse(event_generator(), media_type="text/event-stream")


@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket) -> None:
    await websocket.accept()
    try:
        await websocket.send_text("ready")
        while True:
            data = await websocket.receive_text()
            await websocket.send_text(f"echo: {data}")
    except WebSocketDisconnect:
        logger.info("websocket disconnected")


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
