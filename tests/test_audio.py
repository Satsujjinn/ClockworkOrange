import asyncio

import pytest

from src.audio_streaming.utils import load_audio


@pytest.mark.asyncio
async def test_load_audio(tmp_path):
    dummy = tmp_path / "sound.wav"
    dummy.write_bytes(b"abc")
    data = await load_audio(str(dummy))
    assert data == b"abc"
