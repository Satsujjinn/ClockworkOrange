from src.llm.tab_generator import TabGenerator
from src.api.main import app
from fastapi.testclient import TestClient


def test_generate_tabs():
    tabs = TabGenerator().generate(["C", "G"])
    assert "guitar" in tabs and "bass" in tabs
    assert len(tabs["guitar"]) > 0 and len(tabs["bass"]) > 0


def test_tabs_endpoint():
    with TestClient(app) as client:
        resp = client.post("/tabs", json={"chords": ["C", "G"]})
        assert resp.status_code == 200
        data = resp.json()
        assert "guitar" in data and "bass" in data
