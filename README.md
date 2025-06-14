# Music AI Microservices

This project demonstrates a minimal, production-ready architecture for music AI
services. It follows [Twelve-Factor](https://12factor.net/) principles and uses
FastAPI for scalable inference.

## Structure
- `audio_streaming` – audio utilities & streaming
- `feature_extraction` – feature engineering
- `llm` – chord suggestion model
- `instructions` – songwriting steps generator
- `tabs` – guitar and bass tab generation
- `filter` – signal processing
- `accompaniment` – accompaniment generation
- `api` – FastAPI service
- `utils` – helpers (cache, connection manager)
- `deployment` – Docker Compose and Kubernetes examples

## Development
Use the provided Dockerfile for local development and deployment. The
`ci.yml` workflow illustrates CI/CD aligned with MLOps best practices. See the
comments in the workflow and code for references.

## WebSocket Streaming
The `/ws` endpoint allows streaming audio for realtime chord suggestions.
Send JSON messages with a `data` field containing raw or base64-encoded audio
bytes. The server responds with `{"ack": true}` for each chunk and streams back
chord predictions using messages like `{"chords": ["C", "G", "Am", "F"]}` as soon
as they become available. A heartbeat message `{"type": "ping"}` is also sent
periodically.

## Songwriting Instructions
Musicians without formal theory knowledge can call the `/instructions` endpoint
with a theme to receive step-by-step guidance for composing a simple song.

## Guitar & Bass Tabs
Send a list of chords to `/tabs` to receive simple guitar and bass tabs that
match the progression. This helps players quickly accompany their ideas without
deep theory knowledge.

## References
- MLOps & CI/CD alignment inspired by [Practical MLOps](https://github.com/ai-understanding/practical-mlops)
- Twelve-Factor app containerization best practices
- FastAPI deployment workflow: [FastAPI docs on Docker](https://fastapi.tiangolo.com/deployment/docker/)
- Model monitoring & versioning guidelines: [MLOps with monitoring](https://madewithml.com/courses/mlops/monitoring/)
