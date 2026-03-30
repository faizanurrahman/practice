# Module API Map

This document defines the intended inbound/outbound contracts for each module.
It is intentionally short and is updated as modules are refactored to hexagonal
boundaries.

## auth
- Inbound: `AuthService` (use-case), token issuance, refresh/logout flows
- Outbound: `UserRepositoryPort`, `TokenSignerPort`, `RefreshTokenStorePort`

## users
- Inbound: `UserService` (use-case)
- Outbound: `UserRepositoryPort`, `PasswordHasherPort`

## workspaces
- Inbound: `WorkspaceService`
- Outbound: `WorkspaceRepositoryPort`, `WorkspaceMemberRepositoryPort`
- Policies: `WorkspaceAccessPolicy`

## projects
- Inbound: `ProjectService`
- Outbound: `ProjectRepositoryPort`, `WorkspaceAccessPolicy`
- Events: `ProjectCreatedEvent`

## tasks
- Inbound: `TaskService`
- Outbound: `TaskRepositoryPort`, `ProjectAccessPort`
- Domain service: `TaskHierarchyService`

## attachments
- Inbound: `AttachmentService`
- Outbound: `AttachmentRepositoryPort`, `ObjectStoragePort`, `JobQueuePort`

## storage
- Inbound: `ObjectStoragePort` implementations
- Outbound: Local filesystem / MinIO / S3 adapters

## notifications
- Inbound: `NotificationService`
- Outbound: `NotificationSenderPort`, `JobQueuePort`

## audit
- Inbound: `ActivityLogListener`
- Outbound: `ActivityLogRepositoryPort`

## search
- Inbound: `SearchIndexListener`, `SearchController`
- Outbound: `SearchDocumentRepositoryPort`, `SearchProviderPort`

## analytics
- Inbound: `AnalyticsProjectionListener`
- Outbound: `AnalyticsCounterRepositoryPort`

## eventing
- Inbound: `OutboxService`, `OutboxRelay`
- Outbound: `IntegrationEventPublisher`

## jobs
- Inbound: `JobQueuePort` entrypoints
- Outbound: JobRunr / Redis Streams adapters

## iam
- Inbound: `WorkspaceAccessPolicy`, `ProjectAccessPolicy`, `TaskAccessPolicy`
- Outbound: none

