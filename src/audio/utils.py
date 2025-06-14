"""Audio processing utilities."""

import logging
from typing import Awaitable

import aiofiles

logger = logging.getLogger(__name__)


async def load_audio(file_path: str) -> bytes:
    """Asynchronously load audio file as bytes."""
    logger.info("Loading audio from %s", file_path)
    async with aiofiles.open(file_path, "rb") as f:
        return await f.read()
