from src.llm.instruction_generator import InstructionGenerator
from src.api.main import app
from fastapi.testclient import TestClient


def test_generate_instructions():
    steps = InstructionGenerator().generate("love")
    assert steps
    assert "love" in steps[0].lower()


def test_instructions_endpoint():
    with TestClient(app) as client:
        resp = client.post("/instructions", json={"theme": "love"})
        assert resp.status_code == 200
        data = resp.json()
        assert "steps" in data
        assert any("love" in step.lower() for step in data["steps"])
