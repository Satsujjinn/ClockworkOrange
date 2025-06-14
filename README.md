# Music AI Microservices

This project demonstrates a minimal, production-ready architecture for music AI
services. It follows [Twelve-Factor](https://12factor.net/) principles and uses
FastAPI with Ray Serve for scalable inference.

## Structure
- `audio_streaming` – audio utilities & streaming
- `feature_extraction` – feature engineering
- `llm` – chord suggestion model
- `filter` – signal processing
- `accompaniment` – accompaniment generation
- `api` – FastAPI service
- `utils` – helpers (cache, connection manager)
- `deployment` – Docker Compose and Kubernetes examples

## Development
Use the provided Dockerfile for local development and deployment. The
`ci.yml` workflow illustrates CI/CD aligned with MLOps best practices. See the
comments in the workflow and code for references.

## References
- MLOps & CI/CD alignment inspired by [Practical MLOps](https://github.com/ai-understanding/practical-mlops)
- Ray Serve with FastAPI for scalable inference: [Ray Serve docs](https://docs.ray.io/en/latest/serve/index.html)
- Twelve-Factor app containerization best practices
- FastAPI deployment workflow: [FastAPI docs on Docker](https://fastapi.tiangolo.com/deployment/docker/)
- Model monitoring & versioning guidelines: [MLOps with monitoring](https://madewithml.com/courses/mlops/monitoring/)
