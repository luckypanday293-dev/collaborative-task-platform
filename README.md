# Collaborative Task Platform

A full-stack collaborative work-management application built with **Java 21, Spring Boot, PostgreSQL, and React**. The platform organizes projects, assignments, discussion, and activity history while enforcing role-based permissions.

## What this project demonstrates

- REST API design with Spring Boot controllers and service-layer business logic
- Relational PostgreSQL data model for users, projects, assignments, comments, and activity history
- Database-backed authentication with `ADMIN` and `MEMBER` authorization rules
- Search and filtering across assignment title, description, status, project, and assignee
- Assignment status updates, project activity history, and threaded task comments
- Integration testing against a real PostgreSQL container with Testcontainers
- Automated GitHub Actions workflow for backend tests and frontend production builds
- Responsive React interface for both administrator and team-member workflows

## Architecture

```text
React (Vite)
    |
    | HTTP Basic + JSON REST requests
    v
Spring Boot API
    |-- Spring Security
    |-- Spring Data JPA
    |-- Bean Validation
    v
PostgreSQL
```

## Data model

A human-readable SQL reference is included at `docs/schema.sql`. The running app uses JPA/Hibernate to manage the local schema.

```text
AppUser 1 ---- * Project        (owner)
AppUser 1 ---- * Assignment     (assignee)
Project 1 ---- * Assignment
Assignment 1 -- * Comment
AppUser 1 ---- * Comment        (author)
Project 1 ---- * Activity
AppUser 1 ---- * Activity       (actor)
```

## Main API endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/api/auth/me` | Authenticated | Current user and role |
| GET | `/api/projects` | Authenticated | List projects |
| POST | `/api/projects` | ADMIN | Create project |
| GET | `/api/projects/{id}/activity` | Authenticated | Project activity history |
| GET | `/api/assignments` | Authenticated | Search/filter assignments |
| POST | `/api/assignments` | ADMIN | Create assignment |
| PATCH | `/api/assignments/{id}/status` | Authenticated | Update assignment status |
| GET | `/api/assignments/{id}/comments` | Authenticated | Read discussion |
| POST | `/api/assignments/{id}/comments` | Authenticated | Add comment |

Example filter:

```text
GET /api/assignments?search=dashboard&status=IN_PROGRESS&projectId=1
```

## Local development

### Requirements

- Java 21
- Maven 3.9+
- Node.js 22+
- Docker Desktop or another Docker-compatible runtime

### 1. Start PostgreSQL

From the repository root:

```bash
docker compose up -d
```

The compose file creates a local database named `collab_tasks` on port `5432`.

### 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

### 3. Run the React frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Demo accounts

The local seed script adds two development-only accounts:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Member | `member` | `member123` |

These credentials are intentionally local demo data and must not be used in a deployed production environment.

## Tests

Backend integration tests use Testcontainers to boot a disposable PostgreSQL instance and verify authorization plus assignment filtering:

```bash
cd backend
mvn test
```

Frontend production build:

```bash
cd frontend
npm run build
```

## CI

`.github/workflows/ci.yml` runs on pushes and pull requests. It:

1. Builds the Java 21 backend and runs integration tests.
2. Installs frontend dependencies and creates a production React build.

## Suggested Git history while rebuilding

Use commits that reflect real reconstruction work, for example:

```text
feat: create Spring Boot domain model and PostgreSQL configuration
feat: add role-based security and project endpoints
feat: implement assignment search filters and activity logging
feat: add comments and assignment status workflow
feat: build React task dashboard
 test: add PostgreSQL integration tests
ci: add automated backend and frontend workflow
docs: document local setup and API
```

## Resume summary

**Collaborative Task Platform — Java, Spring Boot, PostgreSQL, React**

- Designed REST endpoints and a relational schema for projects, assignments, comments, users, and activity history.
- Implemented role-based access control, assignment search/filtering, PostgreSQL integration tests, and an automated CI build workflow.
