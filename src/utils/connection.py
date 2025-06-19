import asyncio
from typing import Any, Dict

from fastapi import WebSocket

try:
    from redis import asyncio as redis
except Exception:  # pragma: no cover - optional redis
    redis = None  # type: ignore


class ConnectionManager:
    """Manage websocket connections with Redis pub/sub for scaling.

    Reference: source 7
    """

    def __init__(self, redis: Any | None = None) -> None:
        self.active: Dict[str, WebSocket] = {}
        self.redis = redis

    async def connect(self, websocket: WebSocket) -> None:
        await websocket.accept()
        self.active[websocket.client[0]] = websocket
        if self.redis:
            await self.redis.publish("connect", websocket.client[0])

    async def disconnect(self, websocket: WebSocket) -> None:
        self.active.pop(websocket.client[0], None)
        if self.redis:
            await self.redis.publish("disconnect", websocket.client[0])

    async def send_json(self, websocket: WebSocket, data: Any) -> None:
        await websocket.send_json(data)

    async def broadcast(self, data: Any) -> None:
        for ws in list(self.active.values()):
            await ws.send_json(data)
