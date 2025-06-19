import hashlib
import ujson
from typing import Any, Optional

try:
    from redis import asyncio as redis
except Exception:  # pragma: no cover - optional redis
    redis = None  # type: ignore


class RedisCache:
    """Simple Redis TTL cache for chord suggestions.

    Reference: source 9
    """

    def __init__(self, redis: Any | None, ttl: int = 3600) -> None:
        self.redis = redis
        self.ttl = ttl

    async def get_chords(self, features: list[float]) -> Optional[list[str]]:
        if not self.redis:
            return None
        key = self._key(features)
        data = await self.redis.get(key)
        if data:
            return ujson.loads(data)
        return None

    async def set_chords(self, features: list[float], chords: list[str]) -> None:
        if not self.redis:
            return
        key = self._key(features)
        await self.redis.set(key, ujson.dumps(chords), ex=self.ttl)

    def _key(self, features: list[float]) -> str:
        payload = ujson.dumps(features)
        return "chords:" + hashlib.sha1(payload.encode()).hexdigest()
