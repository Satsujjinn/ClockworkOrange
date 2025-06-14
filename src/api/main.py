import asyncio
import logging
import os
from typing import Any, AsyncGenerator, Dict, List

from starlette.concurrency import run_in_threadpool
from prometheus_client import Histogram, generate_latest

import asyncpg
import structlog
import ujson
from fastapi import Depends, FastAPI, Request, WebSocket, WebSocketDisconnect
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import StreamingResponse, UJSONResponse
from pydantic import BaseModel

from ..utils.connection import ConnectionManager
from ..utils.cache import RedisCache
from ..utils.buffer import AdaptiveBuffer

from ..audio_streaming.utils import load_audio
from ..feature_extraction.mfcc import extract_mfcc
from ..filter.denoise import denoise
from ..llm.chord_suggester import ChordSuggester
from ..accompaniment.generator import AccompanimentGenerator

REQUEST_LATENCY = Histogram(
    "request_latency_seconds",
    "latency of http and websocket requests"
)

try:
    import aioredis
except ImportError:  # pragma: no cover - aioredis may not be installed
    aioredis = None  # type: ignore

logger = structlog.get_logger()

app = FastAPI(
    title="Music AI Service",
    default_response_class=UJSONResponse,  # high performance JSON (source 10)
)
app.add_middleware(GZipMiddleware, minimum_size=1000)  # source 10


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
    else:
        app.state.redis = None
    app.state.manager = ConnectionManager(app.state.redis)
    app.state.cache = RedisCache(app.state.redis)


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


async def get_manager(websocket: WebSocket) -> ConnectionManager:
    return websocket.app.state.manager


async def get_cache(request: Request) -> RedisCache:
    return request.app.state.cache


class MusicService:
    def __init__(self, cache: RedisCache) -> None:
        self.suggester = ChordSuggester()
        self.generator = AccompanimentGenerator()
        self.cache = cache

    async def __call__(self, req: ChordRequest) -> ChordResponse:
        audio_bytes = await load_audio(req.file_path)
        # Async pattern using threadpool for CPU-bound work (source 8)
        features = await run_in_threadpool(extract_mfcc, audio_bytes)
        features = await run_in_threadpool(denoise, features)
        chords = await self.cache.get_chords(features)
        if chords is None:
            chords = await run_in_threadpool(self.suggester.suggest, features)
            await self.cache.set_chords(features, chords)
        accompaniment = await run_in_threadpool(self.generator.generate, chords)
        return ChordResponse(chords=chords, accompaniment=accompaniment)


async def get_service(cache: RedisCache = Depends(get_cache)) -> MusicService:
    return MusicService(cache)


@app.middleware("http")
async def profiling_middleware(request: Request, call_next):
    start = asyncio.get_event_loop().time()
    response = await call_next(request)
    duration = asyncio.get_event_loop().time() - start
    REQUEST_LATENCY.observe(duration)
    logger.info("request", path=request.url.path, duration=duration)
    response.headers["X-Process-Time"] = str(duration)
    return response


@app.get("/healthz")
async def healthz() -> Dict[str, str]:
    return {"status": "ok"}


@app.get("/metrics")
async def metrics() -> UJSONResponse:
    return UJSONResponse(content=generate_latest().decode())


@app.post("/chords", response_model=ChordResponse)
async def suggest_chords(req: ChordRequest, service: MusicService = Depends(get_service)):
    return await service(req)


@app.get("/stream")
async def stream() -> StreamingResponse:
    async def event_generator() -> AsyncGenerator[bytes, None]:
        yield b"data: ready\n\n"
    return StreamingResponse(event_generator(), media_type="text/event-stream")


class WSMessage(BaseModel):
    data: str


async def _heartbeat(ws: WebSocket) -> None:
    while True:
        await asyncio.sleep(10)
        await ws.send_json({"type": "ping"})


async def _process_audio(
    buffer: AdaptiveBuffer, manager: ConnectionManager, ws: WebSocket
) -> None:
    suggester = ChordSuggester()
    async for features in buffer.features():
        chords = suggester.suggest(features)
        await manager.send_json(ws, {"chords": chords})


@app.websocket("/ws")
async def websocket_endpoint(
    websocket: WebSocket,
    manager: ConnectionManager = Depends(get_manager),
) -> None:
    await manager.connect(websocket)
    buffer = AdaptiveBuffer()
    ping_task = asyncio.create_task(_heartbeat(websocket))
    process_task = asyncio.create_task(_process_audio(buffer, manager, websocket))
    try:
        await manager.send_json(websocket, {"ready": True})
        while True:
            data = await websocket.receive_json()
            msg = WSMessage(**data)  # validate
            await buffer.add(msg.data.encode())
            await manager.send_json(websocket, {"ack": True})
    except WebSocketDisconnect:
        logger.info("websocket disconnected")
    finally:
        ping_task.cancel()
        process_task.cancel()
        await manager.disconnect(websocket)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
