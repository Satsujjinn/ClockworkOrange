from src.features.mfcc import extract_mfcc


def test_extract_mfcc():
    mfcc = extract_mfcc(b"data")
    assert len(mfcc) == 13
