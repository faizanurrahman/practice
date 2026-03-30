ALTER TABLE projects
    ADD COLUMN visibility VARCHAR(32) NOT NULL DEFAULT 'WORKSPACE',
    ADD COLUMN archived_at TIMESTAMPTZ;

CREATE INDEX idx_projects_workspace_active ON projects (workspace_id, created_at DESC)
    WHERE archived_at IS NULL;
