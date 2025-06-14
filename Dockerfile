# Twelve-Factor app containerization via Docker
# syntax=docker/dockerfile:1
# Multistage build for smaller images

# --- builder stage ---
FROM python:3.11-slim AS builder
WORKDIR /app
COPY requirements.txt requirements-dev.txt ./
RUN pip install --user -r requirements.txt

# --- runtime stage ---
FROM python:3.11-slim
ENV PYTHONUNBUFFERED=1
WORKDIR /app
COPY --from=builder /root/.local /root/.local
ENV PATH=/root/.local/bin:$PATH
COPY . .
# 12-factor: logs to stdout
CMD ["uvicorn", "webapi.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "2"]
