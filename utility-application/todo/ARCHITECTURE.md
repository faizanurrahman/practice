# Architecture Walkthrough

This project is a **Spring Boot modular monolith** with **Spring Modulith** enforcing
boundaries between feature modules. Each module follows a **hexagonal (ports/adapters)**
layout so infrastructure can evolve without leaking into domain logic.

## Modules and responsibilities

- **auth**: registration/login/refresh, token issuance, refresh token lifecycle.
- **users**: user profile and account state.
- **workspaces**: top-level collaboration boundary and roles.
- **projects**: project lifecycle, visibility, membership policy.
- **tasks**: deep task hierarchy, labels, assignees, and due/start dates.
- **attachments**: file metadata and links to tasks (storage behind port).
- **notifications**: in-app and email channel dispatch (strategy).
- **audit**: immutable activity trail.
- **search**: read model for search documents.
- **analytics**: counters and projections.
- **eventing**: outbox and integration event publishing.
- **jobs**: async dispatch and background jobs.
- **storage**: object storage strategy (local, MinIO, S3).
- **iam**: authorization policy contracts.

## Layering inside a module

```
api/             -> controllers + request/response DTOs
application/     -> use cases, orchestration, ports
domain/          -> entities, policies, domain services
infrastructure/  -> JPA repos, Kafka adapters, storage adapters
```

## Eventing and outbox flow

1. Business use-case writes domain state.
2. Same transaction writes an `outbox_events` record (envelope format).
3. Outbox relay polls, publishes, and marks events as published.
4. Projections update search/audit/analytics.

## Jobs and notifications

- Job requests use a standard envelope (`JobRequest`).
- JobRunr executes jobs now; a Redis Streams adapter stub exists.
- Notifications are dispatched via a `NotificationSenderFactory`:
  - **IN_APP** persists `AppNotification`
  - **EMAIL** logs (stub for mail integration)

## Storage strategy

Object storage is accessed via `ObjectStoragePort`. Implementations:

- **Local** (filesystem) for local development
- **MinIO** (S3-compatible)
- **S3** reserved for future adapter

