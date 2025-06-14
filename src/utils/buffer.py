import asyncio
from collections import deque
from typing import AsyncGenerator, Deque, Iterable, List

from ..feature_extraction.mfcc import extract_mfcc
from ..filter.denoise import denoise


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

    async def features(self, min_size: int = 1024) -> AsyncGenerator[List[float], None]:
        """Yield denoised MFCC features once enough audio has been buffered."""
        data = b""
        async for chunk in self.iterate():
            data += chunk
            if len(data) >= min_size:
                feats = extract_mfcc(data)
                feats = denoise(feats)
                data = b""
                yield feats
