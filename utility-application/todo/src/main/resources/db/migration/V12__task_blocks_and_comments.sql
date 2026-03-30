CREATE TABLE task_blocks (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    owner_user_id UUID NOT NULL REFERENCES users (id),
    block_type VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    sort_order INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_task_blocks_task ON task_blocks (task_id);

CREATE TABLE comments (
    id UUID PRIMARY KEY,
    block_id UUID NOT NULL REFERENCES task_blocks (id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    author_user_id UUID NOT NULL REFERENCES users (id),
    parent_comment_id UUID REFERENCES comments (id),
    body TEXT NOT NULL,
    path VARCHAR(2000) NOT NULL,
    depth INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_comments_block ON comments (block_id);
CREATE INDEX idx_comments_task ON comments (task_id);
