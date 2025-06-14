"""Songwriting instruction generator."""

from typing import List


class InstructionGenerator:
    """Generate simple songwriting instructions."""

    version = "v1"

    def generate(self, theme: str) -> List[str]:
        """Return a basic set of songwriting steps."""
        return [
            f"Start with the theme '{theme}'.",
            "Create a four-chord progression (e.g. C - G - Am - F).",
            "Write a short verse exploring the theme.",
            "Develop a chorus that contrasts the verse.",
            "Layer instrumentation and refine your arrangement.",
        ]
