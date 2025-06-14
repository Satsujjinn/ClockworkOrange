import asyncio
import multiprocessing
from fastapi.testclient import TestClient

from src.api.main import app


def run_server():
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=9000)


def test_startup_shutdown():
    p = multiprocessing.Process(target=run_server)
    p.start()
    try:
        async def wait_ready():
            await asyncio.sleep(1)
        asyncio.run(wait_ready())
        with TestClient(app) as client:
            resp = client.get("/healthz")
            assert resp.status_code == 200
    finally:
        p.terminate()
        p.join()
