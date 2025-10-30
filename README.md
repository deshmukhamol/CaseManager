# CaseManager

A Spring Boot case management REST API built on top of the Flowable CMMN engine. The service allows
case workers to view cases assigned to them, upload supporting documents, and submit actions for
manager approval. A companion React single-page application is included for the case worker UI.

## Features

- Start new case instances backed by the open-source Flowable case management engine
- List cases assigned to a specific user
- Upload and list documents stored on the filesystem for each case
- Submit case actions that trigger the approval stage of the Flowable case model
- React front-end for viewing assigned cases, uploading documents, and sending actions

## Running locally

### Directly on your machine

Run the following commands from the repository root (`CaseManager/`).

```bash
mvn spring-boot:run
```

The application uses an in-memory H2 database. An H2 console is available at
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:casemanager`).

### Using Docker Compose

Launch both the Spring Boot API and the React development server with one command from the repository root:

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

### One-click cloud workspace (Gitpod)

If you would like to try the application in a cloud-hosted development
environment without installing anything locally, you can launch a Gitpod
workspace using the pre-configured setup in this repository:

1. Ensure the project is hosted in a Git provider that Gitpod can access
   (GitHub, GitLab, or Bitbucket).
2. Open `https://gitpod.io/#<repository-url>` in your browser, replacing
   `<repository-url>` with the HTTPS URL of your fork or copy of this project.
3. Gitpod builds a workspace image (defined in `.gitpod.Dockerfile`) that
   includes Maven, OpenJDK 17, Node.js, and Docker Compose.
4. Once the workspace is ready, the provided task automatically runs
   `docker compose up` so the Spring Boot API and React UI start together.
5. Gitpod exposes the React development server on port 5173. Use the “Open
   Browser” button in the port list to open the UI, or copy the provided public
   URL to share the running simulation.

Because Gitpod generates a unique URL for each workspace, you control when and
how long the simulation stays accessible. Shut down the workspace when you are
finished to tear down the temporary environment.

## React web UI

The `ui/` directory contains a Vite-powered React application for case workers.

From the `ui/` directory (inside the repository root), run:

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
