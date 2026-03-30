# Todo backend

Spring Boot API with PostgreSQL, Flyway, JWT access + refresh tokens, projects, recursive tasks (adjacency list), effective due dates, scheduled expiry scans, and deduplicated in-app notifications.

## Prerequisites

- Java 21+
- Docker (for local PostgreSQL), or your own Postgres matching `application.yml`

## Run PostgreSQL

```bash
docker compose up -d
```

Default credentials match [application.yml](src/main/resources/application.yml): database `todo`, user `postgres`, password `ammi`.

## Run the application

```bash
export JWT_SECRET='use-a-long-random-secret-at-least-32-bytes-for-production'
./gradlew bootRun
```

## Tests

CI-friendly smoke tests use H2 (`src/test/resources/application-test.yml`, profile `test`). Production schema is owned by Flyway against PostgreSQL.

```bash
./gradlew test
```

## Docs

- Architecture overview: [ARCHITECTURE.md](ARCHITECTURE.md)
- API examples: [API_EXAMPLES.md](API_EXAMPLES.md)
- Module API map: [MODULE_API_MAP.md](MODULE_API_MAP.md)
- Demo dataset: [demo-data.sql](demo-data.sql)

## API overview

| Method | Path | Notes |
|--------|------|--------|
| POST | `/api/auth/register` | Body: `email`, `password`, `displayName` |
| POST | `/api/auth/login` | Returns `accessToken`, `refreshToken` |
| POST | `/api/auth/refresh` | Body: `refreshToken` (rotates refresh token) |
| POST | `/api/auth/logout` | Body: `refreshToken` (revokes) |
| GET/PATCH | `/api/users/me` | Onboarding: `PATCH` with `{ "onboardingComplete": true }` |
| GET/POST | `/api/workspaces` | Workspaces list + create |
| GET | `/api/workspaces/{id}` | Workspace details |
| CRUD | `/api/projects` | Workspace-scoped projects |
| GET/POST | `/api/projects/{id}/tasks` | Query `parentTaskId` for children; omit for roots |
| GET/PATCH/DELETE | `/api/tasks/{id}` | Text tasks; soft-delete cascades to subtree |
| GET | `/api/tasks/{id}/subtree?maxDepth=` | |
| POST | `/api/tasks/{id}/complete` | Fails if any descendant is not `DONE` |
| POST | `/api/tasks/{id}/move` | Body: `newParentTaskId` (null = root) |
| POST | `/api/projects/{id}/tasks/reorder?parentTaskId=` | Body: `orderedTaskIds` |
| GET/POST | `/api/tasks/{id}/attachments` | Upload/list attachments |
| GET | `/api/attachments/{id}/download` | Download attachment |
| GET | `/api/notifications` | |
| POST | `/api/notifications/{id}/read` | |

Send `Authorization: Bearer <accessToken>` on protected routes.

## Domain notes

- **Effective due**: `COALESCE` chain up to explicit `dueAt`, else project `timelineEndAt` for roots.
- **Notifications**: explicit task `dueAt` fires a task notification; inherited deadlines are covered by the owning ancestor or a single **project timeline** notification (see [plan.md](plan.md)).
