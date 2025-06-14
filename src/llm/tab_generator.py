"""Simple guitar and bass tab generator."""

from typing import Dict, List


class TabGenerator:
    """Generate basic tabs for a chord progression."""

    version = "v1"

    GUITAR_MAP = {
        "C": [
            "e|-0-",
            "B|-1-",
            "G|-0-",
            "D|-2-",
            "A|-3-",
            "E|---",
        ],
        "G": [
            "e|-3-",
            "B|-0-",
            "G|-0-",
            "D|-0-",
            "A|-2-",
            "E|-3-",
        ],
        "Am": [
            "e|-0-",
            "B|-1-",
            "G|-2-",
            "D|-2-",
            "A|-0-",
            "E|---",
        ],
        "F": [
            "e|-1-",
            "B|-1-",
            "G|-2-",
            "D|-3-",
            "A|-3-",
            "E|-1-",
        ],
    }

    BASS_MAP = {
        "C": [
            "G|----",
            "D|----",
            "A|-3--",
            "E|----",
        ],
        "G": [
            "G|----",
            "D|----",
            "A|----",
            "E|-3--",
        ],
        "Am": [
            "G|---",
            "D|---",
            "A|-0-",
            "E|---",
        ],
        "F": [
            "G|---",
            "D|---",
            "A|---",
            "E|-1-",
        ],
    }

    def generate(self, chords: List[str]) -> Dict[str, List[str]]:
        """Return simple guitar and bass tabs."""
        guitar: List[str] = []
        bass: List[str] = []
        for chord in chords:
            g_lines = self.GUITAR_MAP.get(chord, [f"# {chord}"])
            b_lines = self.BASS_MAP.get(chord, [f"# {chord}"])
            guitar.extend(g_lines + [""])
            bass.extend(b_lines + [""])
        return {"guitar": guitar, "bass": bass}
