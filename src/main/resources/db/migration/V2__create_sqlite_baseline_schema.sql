CREATE TABLE IF NOT EXISTS groups (
    id integer primary key,
    description varchar(255),
    name varchar(255) not null unique
);

CREATE TABLE IF NOT EXISTS roles (
    id integer primary key,
    description varchar(255),
    name varchar(255) not null unique
);

CREATE TABLE IF NOT EXISTS task_statuses (
    id integer primary key,
    description varchar(255),
    is_active boolean not null,
    name varchar(255) not null unique,
    status_order integer
);

CREATE TABLE IF NOT EXISTS task_views (
    id integer primary key,
    description varchar(1000),
    is_active boolean not null,
    name varchar(255) not null,
    template_path varchar(255) not null,
    submit_marks_complete boolean not null default 0
);

CREATE TABLE IF NOT EXISTS unit_titles (
    id varchar(255) not null primary key,
    description varchar(255),
    is_active boolean not null,
    name varchar(255) not null,
    weight integer not null
);

CREATE TABLE IF NOT EXISTS users (
    id integer primary key,
    created_at timestamp not null,
    email varchar(255),
    family_name varchar(255),
    given_name varchar(255),
    last_login timestamp,
    name varchar(255),
    open_id_subject varchar(255) not null unique,
    preferred_username varchar(255)
);

CREATE TABLE IF NOT EXISTS tasks (
    id integer primary key,
    created_at timestamp not null,
    default_submission TEXT,
    description TEXT,
    due_date timestamp,
    is_active boolean not null,
    title varchar(255) not null,
    tutorial TEXT,
    created_by_id bigint not null,
    task_view_id bigint,
    unit_title_id varchar(255)
);

CREATE TABLE IF NOT EXISTS user_groups (
    user_id bigint not null,
    group_id bigint not null,
    primary key (user_id, group_id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS task_groups (
    task_id bigint not null,
    group_id bigint not null,
    primary key (task_id, group_id)
);

CREATE TABLE IF NOT EXISTS user_tasks (
    id integer primary key,
    last_modified timestamp not null,
    started_at timestamp,
    status_id bigint not null,
    task_id bigint not null,
    user_id bigint not null
);

CREATE TABLE IF NOT EXISTS task_contents (
    id integer primary key,
    content TEXT,
    is_submitted boolean not null,
    saved_at timestamp not null,
    version integer not null,
    user_task_id bigint not null
);

CREATE TABLE IF NOT EXISTS task_reviews (
    id integer primary key,
    comment TEXT,
    reviewed_at timestamp not null,
    version integer,
    reviewer_id bigint not null,
    status_id bigint not null,
    user_task_id bigint not null
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_tasks_user_id_task_id
    ON user_tasks (user_id, task_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_task_contents_user_task_id_version
    ON task_contents (user_task_id, version);

CREATE UNIQUE INDEX IF NOT EXISTS uk_task_views_template_path
    ON task_views (template_path);
