# CaseManager

A Spring Boot case management REST API built on top of the Flowable CMMN engine. The service allows
case workers to view cases assigned to them, upload supporting documents, and submit actions for
manager approval. A companion React single-page application is included for the case worker UI.
manager approval.

## Features

- Start new case instances backed by the open-source Flowable case management engine
- List cases assigned to a specific user
- Upload and list documents stored on the filesystem for each case
- Submit case actions that trigger the approval stage of the Flowable case model
- React front-end for viewing assigned cases, uploading documents, and sending actions

## Running locally

### Directly on your machine

```bash
mvn spring-boot:run

## Running locally

```bash
./mvnw spring-boot:run
```

The application uses an in-memory H2 database. An H2 console is available at
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:casemanager`).

### Using Docker Compose

Launch both the Spring Boot API and the React development server with one command:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080` and the React UI at
`http://localhost:5173`. Changes to the source code are hot-reloaded by the dev
servers inside the containers, making it easy to simulate the full experience
without installing local toolchains. Press `Ctrl+C` to stop the services when
you are finished.

To customise the allowed CORS origins exposed by the API, set the
`APP_CORS_ALLOWED_ORIGINS` environment variable when starting the container
(`app.cors.allowed-origins` in `application.properties` when running locally).

## React web UI

The `ui/` directory contains a Vite-powered React application for case workers.

```bash
cd ui
npm install
npm run dev
```

By default the development server proxies API requests to `http://localhost:8080`. To point the
client at a different API host, create a `.env` file in `ui/` with `VITE_API_BASE_URL` set to the
desired base URL (for example `VITE_API_BASE_URL=https://my-api.example.com`).

## API overview

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/cases` | Create a new case |
| `GET` | `/api/cases/assigned/{assignee}` | List cases assigned to a user |
| `GET` | `/api/cases/{id}` | Retrieve case details and documents |
| `POST` | `/api/cases/{id}/documents` | Upload a document (multipart/form-data) |
| `GET` | `/api/cases/{id}/documents` | List uploaded documents |
| `POST` | `/api/cases/{id}/actions` | Submit a case action for approval |

### Sample requests

```bash
curl -X POST http://localhost:8080/api/cases \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Onboarding",
        "description": "Client onboarding for ACME",
        "assignee": "case.worker"
      }'

curl http://localhost:8080/api/cases/assigned/case.worker

curl -X POST http://localhost:8080/api/cases/1/documents \
  -F "file=@/path/to/supporting.pdf"

curl -X POST http://localhost:8080/api/cases/1/actions \
  -H "Content-Type: application/json" \
  -d '{
        "action": "REQUEST_APPROVAL",
        "comments": "All documents uploaded"
      }'
```

The Flowable CMMN model automatically creates a human task for the assignee to prepare the case and
activates an approval task once the case is submitted for approval.
