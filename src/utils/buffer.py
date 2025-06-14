import asyncio
from collections import deque
from typing import Deque, Iterable


class AdaptiveBuffer:
    """Adaptive audio chunk buffer for smooth streaming.

    Reference: source 9
    """

    def __init__(self, max_size: int = 10) -> None:
        self.queue: Deque[bytes] = deque(maxlen=max_size)
        self.event = asyncio.Event()

    async def add(self, chunk: bytes) -> None:
        self.queue.append(chunk)
        self.event.set()

    async def iterate(self) -> Iterable[bytes]:
        while True:
            if not self.queue:
                self.event.clear()
                await self.event.wait()
            yield self.queue.popleft()
