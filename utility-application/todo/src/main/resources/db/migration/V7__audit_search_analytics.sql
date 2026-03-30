CREATE TABLE activity_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id UUID REFERENCES users (id) ON DELETE SET NULL,
    action VARCHAR(64) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    entity_id UUID NOT NULL,
    metadata TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activity_logs_entity ON activity_logs (entity_type, entity_id, created_at DESC);

CREATE TABLE search_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doc_type VARCHAR(32) NOT NULL,
    ref_id UUID NOT NULL,
    title TEXT NOT NULL,
    workspace_id UUID,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (doc_type, ref_id)
);

CREATE INDEX idx_search_documents_workspace ON search_documents (workspace_id);

CREATE TABLE analytics_counters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dimension VARCHAR(64) NOT NULL,
    dimension_id UUID NOT NULL,
    metric VARCHAR(64) NOT NULL,
    count_value BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (dimension, dimension_id, metric)
);
