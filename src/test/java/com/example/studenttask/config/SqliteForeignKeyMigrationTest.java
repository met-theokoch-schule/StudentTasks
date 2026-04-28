package com.example.studenttask.config;

import db.migration.V5__add_sqlite_foreign_keys;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqliteForeignKeyMigrationTest {

    @Test
    void migrate_rebuildsRuntimeTablesWithForeignKeys() throws Exception {
        try (Connection connection = openConnection("foreign-key-migration-success")) {
            execute(connection, "PRAGMA foreign_keys = ON");
            createReferenceSchema(connection);
            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'teacher-sub')");
            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (2, 123457, 'student-sub')");
            execute(connection, "INSERT INTO task_views (id, is_active, name, template_path, submit_marks_complete) VALUES (1, 1, 'HTML', 'taskviews/html-css-editor', 0)");
            execute(connection, "INSERT INTO unit_titles (id, is_active, name, weight) VALUES ('html-css', 1, 'HTML & CSS', 10)");
            execute(connection, "INSERT INTO task_statuses (id, is_active, name) VALUES (1, 1, 'IN_BEARBEITUNG')");
            execute(connection, "INSERT INTO groups (id, name) VALUES (1, 'A')");
            execute(connection, "INSERT INTO roles (id, name) VALUES (1, 'ROLE_STUDENT')");
            execute(connection, "INSERT INTO tasks (id, created_at, is_active, title, created_by_id, task_view_id, unit_title_id) VALUES (10, 123456, 1, 'Aufgabe', 1, 1, 'html-css')");
            execute(connection, "INSERT INTO user_tasks (id, last_modified, status_id, task_id, user_id) VALUES (20, 123456, 1, 10, 2)");
            execute(connection, "INSERT INTO task_contents (id, is_submitted, saved_at, version, user_task_id) VALUES (30, 1, 123456, 1, 20)");
            execute(connection, "INSERT INTO task_reviews (id, reviewed_at, version, reviewer_id, status_id, user_task_id) VALUES (40, 123456, 1, 1, 1, 20)");
            execute(connection, "INSERT INTO user_groups (user_id, group_id) VALUES (2, 1)");
            execute(connection, "INSERT INTO user_roles (user_id, role_id) VALUES (2, 1)");
            execute(connection, "INSERT INTO task_groups (task_id, group_id) VALUES (10, 1)");

            new V5__add_sqlite_foreign_keys().migrate(context(connection));

            assertThat(foreignKeyExists(connection, "tasks", "created_by_id", "users", "id")).isTrue();
            assertThat(foreignKeyExists(connection, "tasks", "task_view_id", "task_views", "id")).isTrue();
            assertThat(foreignKeyExists(connection, "user_tasks", "task_id", "tasks", "id")).isTrue();
            assertThat(foreignKeyExists(connection, "task_contents", "user_task_id", "user_tasks", "id")).isTrue();
            assertThat(foreignKeyExists(connection, "task_reviews", "reviewer_id", "users", "id")).isTrue();
            assertThat(foreignKeyExists(connection, "user_roles", "role_id", "roles", "id")).isTrue();

            assertThatThrownBy(() ->
                execute(connection,
                    "INSERT INTO user_roles (user_id, role_id) VALUES (999, 1)"))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("FOREIGN KEY");
        }
    }

    @Test
    void migrate_isIdempotentAfterTablesAlreadyContainForeignKeys() throws Exception {
        try (Connection connection = openConnection("foreign-key-migration-idempotent")) {
            execute(connection, "PRAGMA foreign_keys = ON");
            createReferenceSchema(connection);
            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'teacher-sub')");
            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (2, 123457, 'student-sub')");
            execute(connection, "INSERT INTO task_views (id, is_active, name, template_path, submit_marks_complete) VALUES (1, 1, 'HTML', 'taskviews/html-css-editor', 0)");
            execute(connection, "INSERT INTO unit_titles (id, is_active, name, weight) VALUES ('html-css', 1, 'HTML & CSS', 10)");
            execute(connection, "INSERT INTO task_statuses (id, is_active, name) VALUES (1, 1, 'IN_BEARBEITUNG')");
            execute(connection, "INSERT INTO groups (id, name) VALUES (1, 'A')");
            execute(connection, "INSERT INTO roles (id, name) VALUES (1, 'ROLE_STUDENT')");
            execute(connection, "INSERT INTO tasks (id, created_at, is_active, title, created_by_id, task_view_id, unit_title_id) VALUES (10, 123456, 1, 'Aufgabe', 1, 1, 'html-css')");
            execute(connection, "INSERT INTO user_tasks (id, last_modified, status_id, task_id, user_id) VALUES (20, 123456, 1, 10, 2)");
            execute(connection, "INSERT INTO task_contents (id, is_submitted, saved_at, version, user_task_id) VALUES (30, 1, 123456, 1, 20)");
            execute(connection, "INSERT INTO task_reviews (id, reviewed_at, version, reviewer_id, status_id, user_task_id) VALUES (40, 123456, 1, 1, 1, 20)");
            execute(connection, "INSERT INTO user_groups (user_id, group_id) VALUES (2, 1)");
            execute(connection, "INSERT INTO user_roles (user_id, role_id) VALUES (2, 1)");
            execute(connection, "INSERT INTO task_groups (task_id, group_id) VALUES (10, 1)");

            V5__add_sqlite_foreign_keys migration = new V5__add_sqlite_foreign_keys();
            migration.migrate(context(connection));
            migration.migrate(context(connection));

            assertThat(foreignKeyExists(connection, "user_tasks", "task_id", "tasks", "id")).isTrue();
            assertThat(tableExists(connection, "user_tasks__old")).isFalse();
            assertThat(tableExists(connection, "task_contents__old")).isFalse();
            assertThat(intValue(connection, "SELECT COUNT(*) FROM user_tasks")).isEqualTo(1);
            assertThat(intValue(connection, "SELECT COUNT(*) FROM task_contents")).isEqualTo(1);
            assertThat(intValue(connection, "SELECT COUNT(*) FROM task_reviews")).isEqualTo(1);
        }
    }

    @Test
    void migrate_failsWhenOrphanReferencesExist() throws Exception {
        try (Connection connection = openConnection("foreign-key-migration-orphan")) {
            createReferenceSchema(connection);
            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'teacher-sub')");
            execute(connection, "INSERT INTO task_statuses (id, is_active, name) VALUES (1, 1, 'IN_BEARBEITUNG')");
            execute(connection, "INSERT INTO user_tasks (id, last_modified, status_id, task_id, user_id) VALUES (20, 123456, 1, 999, 1)");

            assertThatThrownBy(() -> new V5__add_sqlite_foreign_keys().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("verwaiste Referenzen")
                .hasMessageContaining("user_tasks.task_id");
        }
    }

    @Test
    void migrate_failsWhenReferencedParentTableIsMissing() throws Exception {
        try (Connection connection = openConnection("foreign-key-migration-missing-parent-table")) {
            execute(connection, "CREATE TABLE tasks (id integer primary key, created_by_id bigint not null)");

            assertThatThrownBy(() -> new V5__add_sqlite_foreign_keys().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Tabelle users fehlt")
                .hasMessageContaining("tasks.created_by_id");
        }
    }

    @Test
    void migrate_failsWhenReferencedForeignKeyColumnIsMissing() throws Exception {
        try (Connection connection = openConnection("foreign-key-migration-missing-column")) {
            execute(connection, "CREATE TABLE users (id integer primary key, created_at timestamp not null, open_id_subject varchar(255) not null unique)");
            execute(connection, "CREATE TABLE tasks (id integer primary key, title varchar(255) not null)");

            assertThatThrownBy(() -> new V5__add_sqlite_foreign_keys().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("FK-Spalten")
                .hasMessageContaining("tasks.created_by_id")
                .hasMessageContaining("users.id");
        }
    }

    private void createReferenceSchema(Connection connection) throws SQLException {
        execute(connection, "CREATE TABLE groups (id integer primary key, description varchar(255), name varchar(255) not null unique)");
        execute(connection, "CREATE TABLE roles (id integer primary key, description varchar(255), name varchar(255) not null unique)");
        execute(connection, "CREATE TABLE task_statuses (id integer primary key, description varchar(255), is_active boolean not null, name varchar(255) not null unique, status_order integer)");
        execute(connection, "CREATE TABLE task_views (id integer primary key, description varchar(1000), is_active boolean not null, name varchar(255) not null, template_path varchar(255) not null, submit_marks_complete boolean not null default 0)");
        execute(connection, "CREATE TABLE unit_titles (id varchar(255) not null primary key, description varchar(255), is_active boolean not null, name varchar(255) not null, weight integer not null)");
        execute(connection, "CREATE TABLE users (id integer primary key, created_at timestamp not null, email varchar(255), family_name varchar(255), given_name varchar(255), last_login timestamp, name varchar(255), open_id_subject varchar(255) not null unique, preferred_username varchar(255))");
        execute(connection, "CREATE TABLE tasks (id integer primary key, created_at timestamp not null, default_submission TEXT, description TEXT, due_date timestamp, is_active boolean not null, title varchar(255) not null, tutorial TEXT, created_by_id bigint not null, task_view_id bigint, unit_title_id varchar(255))");
        execute(connection, "CREATE TABLE user_groups (user_id bigint not null, group_id bigint not null, primary key (user_id, group_id))");
        execute(connection, "CREATE TABLE user_roles (user_id bigint not null, role_id bigint not null, primary key (user_id, role_id))");
        execute(connection, "CREATE TABLE task_groups (task_id bigint not null, group_id bigint not null, primary key (task_id, group_id))");
        execute(connection, "CREATE TABLE user_tasks (id integer primary key, last_modified timestamp not null, started_at timestamp, status_id bigint not null, task_id bigint not null, user_id bigint not null)");
        execute(connection, "CREATE UNIQUE INDEX uk_user_tasks_user_id_task_id ON user_tasks (user_id, task_id)");
        execute(connection, "CREATE TABLE task_contents (id integer primary key, content TEXT, is_submitted boolean not null, saved_at timestamp not null, version integer not null, user_task_id bigint not null)");
        execute(connection, "CREATE UNIQUE INDEX uk_task_contents_user_task_id_version ON task_contents (user_task_id, version)");
        execute(connection, "CREATE TABLE task_reviews (id integer primary key, comment TEXT, reviewed_at timestamp not null, version integer, reviewer_id bigint not null, status_id bigint not null, user_task_id bigint not null)");
    }

    private Connection openConnection(String name) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:file:" + name + "?mode=memory&cache=shared");
    }

    private Context context(Connection connection) {
        return new Context() {
            @Override
            public Configuration getConfiguration() {
                return null;
            }

            @Override
            public Connection getConnection() {
                return connection;
            }
        };
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean foreignKeyExists(
            Connection connection,
            String tableName,
            String fromColumn,
            String targetTable,
            String targetColumn) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_key_list(" + tableName + ")")) {
            while (resultSet.next()) {
                if (fromColumn.equalsIgnoreCase(resultSet.getString("from"))
                    && targetTable.equalsIgnoreCase(resultSet.getString("table"))
                    && targetColumn.equalsIgnoreCase(resultSet.getString("to"))) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'")) {
            return resultSet.next();
        }
    }

    private int intValue(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
