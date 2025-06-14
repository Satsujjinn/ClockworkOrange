from src.accompaniment.generator import AccompanimentGenerator


def test_generate():
    chords = ["C", "G"]
    assert AccompanimentGenerator().generate(chords) == ["C-strum", "G-strum"]
