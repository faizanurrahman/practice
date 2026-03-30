ALTER TABLE tasks
    ADD COLUMN path VARCHAR(2000),
    ADD COLUMN depth INT NOT NULL DEFAULT 0,
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

UPDATE tasks
SET path = REPLACE(id::text, '-', ''),
    depth = 0
WHERE deleted_at IS NOT NULL;

WITH RECURSIVE hier AS (
    SELECT id,
           parent_task_id,
           project_id,
           sort_order,
           deleted_at,
           LPAD(sort_order::text, 10, '0') AS path,
           0                             AS depth
    FROM tasks
    WHERE parent_task_id IS NULL
      AND deleted_at IS NULL
    UNION ALL
    SELECT t.id,
           t.parent_task_id,
           t.project_id,
           t.sort_order,
           t.deleted_at,
           h.path || '.' || LPAD(t.sort_order::text, 10, '0'),
           h.depth + 1
    FROM tasks t
             INNER JOIN hier h ON t.parent_task_id = h.id
    WHERE t.deleted_at IS NULL
)
UPDATE tasks
SET path = h.path,
    depth = h.depth
FROM hier h
WHERE tasks.id = h.id
  AND tasks.deleted_at IS NULL;

UPDATE tasks
SET path = REPLACE(id::text, '-', ''),
    depth = 0
WHERE path IS NULL;

ALTER TABLE tasks
    ALTER COLUMN path SET NOT NULL;

CREATE INDEX idx_tasks_project_path ON tasks (project_id, path) WHERE deleted_at IS NULL;
