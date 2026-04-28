package com.example.studenttask.config;

import db.migration.V4__remove_legacy_runtime_artifacts;
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

class SqliteLegacyRuntimeCleanupMigrationTest {

    @Test
    void migrate_backfillsTaskViewReferenceAndDropsLegacySubmissions() throws Exception {
        try (Connection connection = openConnection("legacy-runtime-cleanup-success")) {
            execute(connection, "CREATE TABLE tasks (id integer primary key, task_view_id bigint, view_type_id bigint)");
            execute(connection, "INSERT INTO tasks (id, task_view_id, view_type_id) VALUES (1, NULL, 7)");
            execute(connection, """
                CREATE TABLE task_contents (
                    id integer primary key,
                    user_task_id bigint not null,
                    version integer not null,
                    is_submitted boolean not null
                )
                """);
            execute(connection, """
                CREATE TABLE submissions (
                    id integer primary key,
                    submitted_at timestamp not null,
                    version integer not null,
                    task_content_id bigint not null,
                    user_task_id bigint not null
                )
                """);
            execute(connection,
                "INSERT INTO task_contents (id, user_task_id, version, is_submitted) VALUES (10, 3, 2, 1)");
            execute(connection,
                "INSERT INTO submissions (id, submitted_at, version, task_content_id, user_task_id) VALUES (1, 123456, 2, 10, 3)");

            new V4__remove_legacy_runtime_artifacts().migrate(context(connection));

            assertThat(columnExists(connection, "tasks", "view_type_id")).isFalse();
            assertThat(intValue(connection, "SELECT task_view_id FROM tasks WHERE id = 1")).isEqualTo(7);
            assertThat(tableExists(connection, "submissions")).isFalse();
        }
    }

    @Test
    void migrate_keepsTaskViewReferenceWhenLegacyAndCanonicalIdsAlreadyMatch() throws Exception {
        try (Connection connection = openConnection("legacy-runtime-cleanup-matching-task-view-ids")) {
            execute(connection, "CREATE TABLE tasks (id integer primary key, task_view_id bigint, view_type_id bigint)");
            execute(connection, "INSERT INTO tasks (id, task_view_id, view_type_id) VALUES (1, 7, 7)");

            new V4__remove_legacy_runtime_artifacts().migrate(context(connection));

            assertThat(columnExists(connection, "tasks", "view_type_id")).isFalse();
            assertThat(intValue(connection, "SELECT task_view_id FROM tasks WHERE id = 1")).isEqualTo(7);
        }
    }

    @Test
    void migrate_failsWhenLegacyTaskViewReferencesConflict() throws Exception {
        try (Connection connection = openConnection("legacy-runtime-cleanup-task-view-conflict")) {
            execute(connection, "CREATE TABLE tasks (id integer primary key, task_view_id bigint, view_type_id bigint)");
            execute(connection, "INSERT INTO tasks (id, task_view_id, view_type_id) VALUES (1, 4, 7)");

            assertThatThrownBy(() -> new V4__remove_legacy_runtime_artifacts().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("task_view_id")
                .hasMessageContaining("view_type_id");
        }
    }

    @Test
    void migrate_failsWhenSubmissionsExistWithoutTaskContentsTable() throws Exception {
        try (Connection connection = openConnection("legacy-runtime-cleanup-missing-task-contents")) {
            execute(connection, """
                CREATE TABLE submissions (
                    id integer primary key,
                    submitted_at timestamp not null,
                    version integer not null,
                    task_content_id bigint not null,
                    user_task_id bigint not null
                )
                """);

            assertThatThrownBy(() -> new V4__remove_legacy_runtime_artifacts().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("submissions")
                .hasMessageContaining("task_contents fehlt");
        }
    }

    @Test
    void migrate_failsWhenLegacySubmissionsAreNotMirroredBySubmittedTaskContents() throws Exception {
        try (Connection connection = openConnection("legacy-runtime-cleanup-submission-conflict")) {
            execute(connection, """
                CREATE TABLE task_contents (
                    id integer primary key,
                    user_task_id bigint not null,
                    version integer not null,
                    is_submitted boolean not null
                )
                """);
            execute(connection, """
                CREATE TABLE submissions (
                    id integer primary key,
                    submitted_at timestamp not null,
                    version integer not null,
                    task_content_id bigint not null,
                    user_task_id bigint not null
                )
                """);
            execute(connection,
                "INSERT INTO task_contents (id, user_task_id, version, is_submitted) VALUES (10, 3, 2, 0)");
            execute(connection,
                "INSERT INTO submissions (id, submitted_at, version, task_content_id, user_task_id) VALUES (1, 123456, 2, 10, 3)");

            assertThatThrownBy(() -> new V4__remove_legacy_runtime_artifacts().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("submissions")
                .hasMessageContaining("task_contents");
        }
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

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'")) {
            return resultSet.next();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (resultSet.next()) {
                if (columnName.equalsIgnoreCase(resultSet.getString("name"))) {
                    return true;
                }
            }
            return false;
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
