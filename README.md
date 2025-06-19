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
- `ClockworkRed` – Android client
- `utils` – helpers (cache, connection manager)
- `deployment` – Docker Compose and Kubernetes examples

## Development
Use the provided Dockerfile for local development and deployment. The
`ci.yml` workflow illustrates CI/CD aligned with MLOps best practices. See the
comments in the workflow and code for references.

## Setup

1. Create and activate a Python 3.11 virtual environment:

   ```bash
   python -m venv venv
   source venv/bin/activate
   ```

2. Install backend requirements:

   ```bash
   pip install -r requirements.txt
   ```

   Development and test tools can be installed with:

   ```bash
   pip install -r requirements-dev.txt
   ```

3. Start the API service:

   ```bash
   uvicorn src.api.main:app --reload
   ```

   Or run with Docker:

   ```bash
   docker build -t music-api .
   docker run -p 8000:8000 music-api
   ```

4. Launch the React frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. Install the Android client on a device or emulator:

   ```bash
   cd ClockworkRed
   ./gradlew installDebug
   ```

   Network requests use the `BASE_URL` value from
   `ClockworkRed/data/build.gradle.kts`. Edit this property to point the
   Android client at your backend service.

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

For mobile development, see the `ClockworkRed` directory for an Android client built with Kotlin and Jetpack Compose.

## Release

1. Run the full test suite to ensure the code is stable:

   ```bash
   pytest -vv
   ```

2. Build and push the API Docker image:

   ```bash
   docker build -t your-registry/music-api:latest .
   docker push your-registry/music-api:latest
   ```

   Deploy locally with Docker Compose:

   ```bash
   docker compose -f deployment/docker-compose.yml up
   ```

   Or deploy to Kubernetes:

   ```bash
   kubectl apply -f deployment/k8s/deployment.yaml
   ```

3. Generate a production build of the frontend:

   ```bash
   cd frontend
   npm install
   npm run build
   ```

4. Build the Android release APK:

   ```bash
   cd ClockworkRed
   ./gradlew assembleRelease
   ```
   The APK will be in `app/build/outputs/apk/release`.
