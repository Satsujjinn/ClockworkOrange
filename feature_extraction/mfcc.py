"""Feature extraction functions."""

from typing import List


def extract_mfcc(audio_bytes: bytes) -> List[float]:
    """Mock MFCC extraction.

    Replace with librosa or torchaudio in production.
    """
    return [0.0 for _ in range(13)]
