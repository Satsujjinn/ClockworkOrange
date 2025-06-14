"""Ray Serve + FastAPI example for scalable inference.
Web API with FastAPI and Ray Serve.

This example uses Ray Serve for autoscaling inference endpoints.
See https://docs.ray.io/en/latest/serve/index.html for details.
"""
"""
"""

import logging
import os
from typing import Dict

from fastapi import FastAPI
from pydantic import BaseModel
from prometheus_client import Counter, Histogram, generate_latest

from audio.utils import load_audio
from feature_extraction.mfcc import extract_mfcc
from llm.chord_suggester import ChordSuggester
from filter.denoise import denoise
from accompaniment.generator import AccompanimentGenerator

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

REQUEST_COUNT = Counter("request_count", "Total requests")
REQUEST_LATENCY = Histogram("request_latency_seconds", "Request latency")

app = FastAPI(title="Music AI Service")

@app.on_event("shutdown")
def shutdown_event() -> None:
    logger.info("Shutting down gracefully")


class ChordRequest(BaseModel):
    """Input model validated by Pydantic."""

    file_path: str


class MusicService:
    """Core logic for chord suggestion and accompaniment."""

    def __init__(self) -> None:
        self.suggester = ChordSuggester()
        self.generator = AccompanimentGenerator()

    async def __call__(self, req: ChordRequest) -> Dict[str, list]:
        audio_bytes = load_audio(req.file_path)
        features = extract_mfcc(audio_bytes)
        features = denoise(features)
        chords = self.suggester.suggest(features)
        accompaniment = self.generator.generate(chords)
        return {"chords": chords, "accompaniment": accompaniment}

service = MusicService()

@app.get("/healthz")
def healthz() -> Dict[str, str]:
    return {"status": "ok"}


@app.get("/metrics")
def metrics() -> str:
    return generate_latest().decode()


@app.post("/chords")
async def suggest_chords(req: ChordRequest) -> Dict[str, list]:
    REQUEST_COUNT.inc()
    with REQUEST_LATENCY.time():
        return await service(req)


if __name__ == "__main__":
    if os.getenv("USE_RAY_SERVE", "0") == "1":
        from ray import serve

        @serve.deployment
        @serve.ingress(app)
        class ServeMusicService(MusicService):
            pass

        serve.run(ServeMusicService.bind(), address=os.getenv("RAY_ADDRESS", "auto"))
    else:
        import uvicorn

        uvicorn.run(app, host="0.0.0.0", port=8000)
