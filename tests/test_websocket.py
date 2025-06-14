import pytest
from fastapi.testclient import TestClient

from src.api.main import app


@pytest.mark.asyncio
async def test_websocket_echo():
    with TestClient(app) as client:
        with client.websocket_connect("/ws") as ws:
            ready = ws.receive_json()
            assert ready.get("ready") is True
            ws.send_json({"data": "hi"})
            resp = ws.receive_json()
            assert resp.get("ack") is True


@pytest.mark.asyncio
async def test_websocket_midi():
    with TestClient(app) as client:
        with client.websocket_connect("/ws/midi") as ws:
            ready = ws.receive_json()
            assert ready.get("ready") is True
            ws.send_json({"chords": ["C", "G"]})
            msg = ws.receive_json()
            assert msg["type"] == "note_on"
            assert "note" in msg
