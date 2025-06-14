"""Accompaniment generation."""

from typing import List


class AccompanimentGenerator:
    """Generates accompaniment patterns."""

    def generate(self, chords: List[str]) -> List[str]:
        """Mock accompaniment generation."""
        return [c + "-strum" for c in chords]
