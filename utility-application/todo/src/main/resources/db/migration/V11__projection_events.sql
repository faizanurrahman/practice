CREATE TABLE projection_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    projection VARCHAR(120) NOT NULL,
    event_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (projection, event_id)
);

CREATE INDEX idx_projection_events_projection ON projection_events (projection, created_at DESC);
