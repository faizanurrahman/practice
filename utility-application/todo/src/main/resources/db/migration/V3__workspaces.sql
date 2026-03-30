CREATE TABLE workspaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    created_by_user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE workspace_members (
    workspace_id UUID NOT NULL REFERENCES workspaces (id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role VARCHAR(32) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (workspace_id, user_id)
);

CREATE INDEX idx_workspace_members_user ON workspace_members (user_id);

ALTER TABLE projects ADD COLUMN workspace_id UUID REFERENCES workspaces (id);

INSERT INTO workspaces (id, name, created_by_user_id)
SELECT gen_random_uuid(), 'Personal', id FROM users;

INSERT INTO workspace_members (workspace_id, user_id, role)
SELECT w.id, w.created_by_user_id, 'OWNER'
FROM workspaces w;

UPDATE projects p
SET workspace_id = w.id
FROM workspaces w
WHERE w.created_by_user_id = p.user_id;

ALTER TABLE projects ALTER COLUMN workspace_id SET NOT NULL;

ALTER TABLE projects DROP CONSTRAINT IF EXISTS projects_user_id_fkey;
ALTER TABLE projects DROP COLUMN user_id;

DROP INDEX IF EXISTS idx_projects_user;

CREATE INDEX idx_projects_workspace ON projects (workspace_id);
