ALTER TABLE tasks
    ADD COLUMN assignee_user_id UUID REFERENCES users (id),
    ADD COLUMN start_at TIMESTAMPTZ;

CREATE INDEX idx_tasks_assignee ON tasks (assignee_user_id) WHERE deleted_at IS NULL;

CREATE TABLE labels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspaces (id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    color VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uniq_labels_workspace_name
    ON labels (workspace_id, lower(name));

CREATE TABLE task_labels (
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    label_id UUID NOT NULL REFERENCES labels (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (task_id, label_id)
);

CREATE INDEX idx_task_labels_label ON task_labels (label_id);
