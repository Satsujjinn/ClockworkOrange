"""Audio processing utilities."""

import logging

logger = logging.getLogger(__name__)


def load_audio(file_path: str) -> bytes:
    """Load audio file as bytes.

    In production, integrate with streaming service.
    """
    logger.info("Loading audio from %s", file_path)
    with open(file_path, "rb") as f:
        return f.read()
