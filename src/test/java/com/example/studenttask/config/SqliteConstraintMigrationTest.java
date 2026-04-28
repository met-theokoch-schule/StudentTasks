package com.example.studenttask.config;

import db.migration.V1__add_runtime_unique_constraints;
import db.migration.V3__add_task_views_submit_marks_complete_column;
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

class SqliteConstraintMigrationTest {

    @Test
    void migrate_createsExpectedUniqueIndexesOnExistingTables() throws Exception {
        try (Connection connection = openConnection("constraint-migration-success")) {
            execute(connection, "CREATE TABLE user_tasks (id integer primary key, user_id bigint not null, task_id bigint not null)");
            execute(connection, "CREATE TABLE task_contents (id integer primary key, user_task_id bigint not null, version integer not null)");
            execute(connection, "CREATE TABLE task_views (id integer primary key, template_path varchar(255) not null)");

            new V1__add_runtime_unique_constraints().migrate(context(connection));

            assertThat(indexExists(connection, "uk_user_tasks_user_id_task_id")).isTrue();
            assertThat(indexExists(connection, "uk_task_contents_user_task_id_version")).isTrue();
            assertThat(indexExists(connection, "uk_task_views_template_path")).isTrue();
        }
    }

    @Test
    void migrate_failsWhenDuplicateAssignmentsExist() throws Exception {
        try (Connection connection = openConnection("constraint-migration-duplicates")) {
            execute(connection, "CREATE TABLE user_tasks (id integer primary key, user_id bigint not null, task_id bigint not null)");
            execute(connection, "INSERT INTO user_tasks (id, user_id, task_id) VALUES (1, 10, 20)");
            execute(connection, "INSERT INTO user_tasks (id, user_id, task_id) VALUES (2, 10, 20)");

            assertThatThrownBy(() -> new V1__add_runtime_unique_constraints().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("uk_user_tasks_user_id_task_id")
                .hasMessageContaining("doppelte user_tasks-Zuordnungen");
        }
    }

    @Test
    void migrate_failsWhenDuplicateTaskContentVersionsExist() throws Exception {
        try (Connection connection = openConnection("constraint-migration-task-content-duplicates")) {
            execute(connection, "CREATE TABLE task_contents (id integer primary key, user_task_id bigint not null, version integer not null)");
            execute(connection, "INSERT INTO task_contents (id, user_task_id, version) VALUES (1, 10, 2)");
            execute(connection, "INSERT INTO task_contents (id, user_task_id, version) VALUES (2, 10, 2)");

            assertThatThrownBy(() -> new V1__add_runtime_unique_constraints().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("uk_task_contents_user_task_id_version")
                .hasMessageContaining("doppelte task_contents-Versionen");
        }
    }

    @Test
    void migrate_failsWhenDuplicateTaskViewTemplatePathsExist() throws Exception {
        try (Connection connection = openConnection("constraint-migration-task-view-duplicates")) {
            execute(connection, "CREATE TABLE task_views (id integer primary key, template_path varchar(255) not null)");
            execute(connection, "INSERT INTO task_views (id, template_path) VALUES (1, 'taskviews/html-css-editor')");
            execute(connection, "INSERT INTO task_views (id, template_path) VALUES (2, 'taskviews/html-css-editor')");

            assertThatThrownBy(() -> new V1__add_runtime_unique_constraints().migrate(context(connection)))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("uk_task_views_template_path")
                .hasMessageContaining("doppelte task_views.template_path-Werte");
        }
    }

    @Test
    void migrate_addsSubmitMarksCompleteColumnToExistingTaskViews() throws Exception {
        try (Connection connection = openConnection("task-views-submit-marks-complete")) {
            execute(connection, "CREATE TABLE task_views (id integer primary key, name varchar(255) not null, "
                + "description varchar(1000), is_active boolean not null, template_path varchar(255) not null)");
            execute(connection, "INSERT INTO task_views (id, name, description, is_active, template_path) "
                + "VALUES (1, 'HTML+CSS Editor', 'legacy', 1, 'taskviews/html-css-editor')");

            new V3__add_task_views_submit_marks_complete_column().migrate(context(connection));

            assertThat(columnExists(connection, "task_views", "submit_marks_complete")).isTrue();
            assertThat(intValue(connection,
                "SELECT submit_marks_complete FROM task_views WHERE id = 1")).isEqualTo(0);
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

    private boolean indexExists(Connection connection, String indexName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT 1 FROM sqlite_master WHERE type = 'index' AND name = '" + indexName + "'")) {
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
