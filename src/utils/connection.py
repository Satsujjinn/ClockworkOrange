import asyncio
from typing import Any, Dict
from uuid import uuid4

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
        cid = uuid4().hex
        websocket.state.connection_id = cid
        self.active[cid] = websocket
        if self.redis:
            await self.redis.publish("connect", cid)

    async def disconnect(self, websocket: WebSocket) -> None:
        cid = getattr(websocket.state, "connection_id", None)
        if cid:
            self.active.pop(cid, None)
            if self.redis:
                await self.redis.publish("disconnect", cid)

    async def send_json(self, websocket: WebSocket, data: Any) -> None:
        await websocket.send_json(data)

    async def broadcast(self, data: Any) -> None:
        for ws in list(self.active.values()):
            await ws.send_json(data)
