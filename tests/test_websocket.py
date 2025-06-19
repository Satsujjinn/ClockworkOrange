from fastapi.testclient import TestClient

from src.api.main import app


def test_websocket_echo():
    with TestClient(app) as client:
        with client.websocket_connect("/ws") as ws:
            ready = ws.receive_json()
            assert ready.get("ready") is True
            ws.send_json({"data": "a" * 2048})
            resp = ws.receive_json()
            assert resp.get("ack") is True
            chords_msg = ws.receive_json()
            assert chords_msg.get("chords") == ["C", "G", "Am", "F"]
