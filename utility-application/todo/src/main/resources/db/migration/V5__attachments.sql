CREATE TABLE attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    uploaded_by_user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    object_key VARCHAR(512) NOT NULL,
    original_filename VARCHAR(500) NOT NULL,
    content_type VARCHAR(200),
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_attachments_task ON attachments (task_id);
