import sys
import pathlib
sys.path.append(str(pathlib.Path(__file__).resolve().parents[1]))

import pytest
from fastapi.testclient import TestClient
from src.main import app


def test_healthz() -> None:
    with TestClient(app) as client:
        resp = client.get("/healthz")
        assert resp.status_code == 200
        assert resp.json() == {"status": "ok"}


def test_suggest_chords(tmp_path) -> None:
    dummy = tmp_path / "sound.wav"
    dummy.write_bytes(b"\x00")
    with TestClient(app) as client:
        resp = client.post("/chords", json={"file_path": str(dummy)})
        assert resp.status_code == 200
        assert "chords" in resp.json()
