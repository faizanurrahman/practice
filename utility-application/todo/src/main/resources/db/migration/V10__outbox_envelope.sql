ALTER TABLE outbox_events
    ADD COLUMN event_id UUID,
    ADD COLUMN event_version INT NOT NULL DEFAULT 1,
    ADD COLUMN source VARCHAR(200),
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN last_error TEXT,
    ADD COLUMN next_attempt_at TIMESTAMPTZ;

UPDATE outbox_events
SET event_id = gen_random_uuid()
WHERE event_id IS NULL;

ALTER TABLE outbox_events
    ALTER COLUMN event_id SET NOT NULL;

CREATE INDEX idx_outbox_unpublished
    ON outbox_events (published_at, next_attempt_at, created_at);
