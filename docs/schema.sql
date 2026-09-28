-- Reference schema for the JPA model. Hibernate manages the local development schema.

CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER'))
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    due_date DATE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    assignee_id BIGINT REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    body VARCHAR(2000) NOT NULL,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE activity_history (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(80) NOT NULL,
    details VARCHAR(1000) NOT NULL,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    actor_id BIGINT NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_assignments_project ON assignments(project_id);
CREATE INDEX idx_assignments_assignee ON assignments(assignee_id);
CREATE INDEX idx_assignments_status ON assignments(status);
CREATE INDEX idx_comments_assignment ON comments(assignment_id);
CREATE INDEX idx_activity_project_created ON activity_history(project_id, created_at DESC);
