from src.filter.denoise import denoise


def test_denoise():
    data = [1.0, 2.0]
    assert denoise(data) == data
