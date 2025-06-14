from src.llm.chord_suggester import ChordSuggester


def test_suggest():
    chords = ChordSuggester().suggest([0.0] * 13)
    assert chords == ["C", "G", "Am", "F"]
