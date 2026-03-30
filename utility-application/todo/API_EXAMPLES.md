# API Examples

Replace `BASE_URL` with your server URL (e.g. `http://localhost:8080`).

## Register + Login

```bash
curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "SecurePass123",
    "displayName": "Demo User"
  }'
```

```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "r1x..."
}
```

## Create workspace

```bash
curl -s -X POST "$BASE_URL/api/workspaces" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "name": "Demo Workspace" }'
```

## Create project (visibility + timeline)

```bash
curl -s -X POST "$BASE_URL/api/projects" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Demo Project",
    "description": "Learn modular monolith design",
    "timelineEndAt": "2026-12-31T23:59:59Z",
    "visibility": "WORKSPACE"
  }'
```

## Create task (labels + assignee + dates)

```bash
curl -s -X POST "$BASE_URL/api/projects/$PROJECT_ID/tasks" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Initial task",
    "content": "This is the root task",
    "startAt": "2026-06-10T12:00:00Z",
    "dueAt": "2026-06-15T12:00:00Z",
    "assigneeUserId": null,
    "labels": ["Urgent", "Backend"]
  }'
```

## Patch task

```bash
curl -s -X PATCH "$BASE_URL/api/tasks/$TASK_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS",
    "labels": ["Urgent"],
    "clearDueAt": false
  }'
```

## Upload attachment

```bash
curl -s -X POST "$BASE_URL/api/tasks/$TASK_ID/attachments" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "file=@./example.pdf"
```

## Notifications

```bash
curl -s -X GET "$BASE_URL/api/notifications" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

