ALTER TABLE tasks
    ADD COLUMN IF NOT EXISTS priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM';

CREATE TABLE task_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users (id),
    role VARCHAR(16) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (task_id, user_id)
);

CREATE INDEX idx_task_members_task ON task_members (task_id);
CREATE INDEX idx_task_members_user ON task_members (user_id);

CREATE TABLE task_join_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    requester_id UUID NOT NULL REFERENCES users (id),
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    message TEXT,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    decided_at TIMESTAMPTZ,
    decided_by_user_id UUID REFERENCES users (id)
);

CREATE INDEX idx_join_requests_task ON task_join_requests (task_id);

CREATE TABLE subtask_proposals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    proposer_id UUID NOT NULL REFERENCES users (id),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(16) NOT NULL DEFAULT 'PROPOSED',
    proposed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    decided_at TIMESTAMPTZ,
    decided_by_user_id UUID REFERENCES users (id)
);

CREATE INDEX idx_subtask_proposals_parent ON subtask_proposals (parent_task_id);

INSERT INTO task_members (id, task_id, user_id, role, joined_at)
SELECT gen_random_uuid(),
       t.id,
       w.created_by_user_id,
       'OWNER',
       t.created_at
FROM tasks t
JOIN projects p ON p.id = t.project_id
JOIN workspaces w ON w.id = p.workspace_id
WHERE t.deleted_at IS NULL
ON CONFLICT (task_id, user_id) DO NOTHING;

INSERT INTO task_members (id, task_id, user_id, role, joined_at)
SELECT gen_random_uuid(),
       t.id,
       t.assignee_user_id,
       'MEMBER',
       t.created_at
FROM tasks t
WHERE t.deleted_at IS NULL
  AND t.assignee_user_id IS NOT NULL
ON CONFLICT (task_id, user_id) DO NOTHING;
