-- Demo data for local development
-- Uses pgcrypto's crypt() for password hashing (extension created in V1).

WITH demo_user AS (
    INSERT INTO users (id, email, password_hash, display_name)
    VALUES (gen_random_uuid(),
            'demo@example.com',
            crypt('SecurePass123', gen_salt('bf')),
            'Demo User')
    RETURNING id
),
ws AS (
    INSERT INTO workspaces (id, name, created_by_user_id)
    SELECT gen_random_uuid(), 'Demo Workspace', id FROM demo_user
    RETURNING id, created_by_user_id
),
wm AS (
    INSERT INTO workspace_members (workspace_id, user_id, role)
    SELECT id, created_by_user_id, 'OWNER' FROM ws
),
pr AS (
    INSERT INTO projects (id, workspace_id, name, description, visibility)
    SELECT gen_random_uuid(), id, 'Demo Project', 'Seed data', 'WORKSPACE' FROM ws
    RETURNING id, workspace_id
),
label AS (
    INSERT INTO labels (id, workspace_id, name, color)
    SELECT gen_random_uuid(), workspace_id, 'Urgent', '#e11d48' FROM pr
    RETURNING id
),
task_root AS (
    INSERT INTO tasks (id, project_id, title, content, status, sort_order, path, depth)
    SELECT gen_random_uuid(), pr.id, 'Root Task', 'Seeded root task', 'TODO', 0, '0000000000', 0 FROM pr
    RETURNING id, project_id
),
task_child AS (
    INSERT INTO tasks (id, project_id, parent_task_id, title, content, status, sort_order, path, depth)
    SELECT gen_random_uuid(), task_root.project_id, task_root.id, 'Subtask', 'Seeded subtask',
           'TODO', 0, '0000000000.0000000000', 1 FROM task_root
    RETURNING id
)
INSERT INTO task_labels (task_id, label_id)
SELECT task_root.id, label.id FROM task_root, label;
