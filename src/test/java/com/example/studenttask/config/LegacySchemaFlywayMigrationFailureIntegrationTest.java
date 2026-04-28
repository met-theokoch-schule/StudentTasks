package com.example.studenttask.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class LegacySchemaFlywayMigrationFailureIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void localProfile_blocksStartupWhenLegacyUserTaskAssignmentsContainDuplicates() throws Exception {
        Path databasePath = tempDir.resolve("duplicate-user-tasks.db");
        try (Connection connection = openConnection(databasePath)) {
            execute(connection,
                "CREATE TABLE user_tasks (id integer primary key, user_id bigint not null, task_id bigint not null)");
            execute(connection, "INSERT INTO user_tasks (id, user_id, task_id) VALUES (1, 10, 20)");
            execute(connection, "INSERT INTO user_tasks (id, user_id, task_id) VALUES (2, 10, 20)");
        }

        assertMigrationFailure(databasePath, "uk_user_tasks_user_id_task_id", "doppelte user_tasks-Zuordnungen");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyTaskContentVersionsContainDuplicates() throws Exception {
        Path databasePath = tempDir.resolve("duplicate-task-contents.db");
        try (Connection connection = openConnection(databasePath)) {
            execute(connection,
                "CREATE TABLE task_contents (id integer primary key, user_task_id bigint not null, version integer not null)");
            execute(connection, "INSERT INTO task_contents (id, user_task_id, version) VALUES (1, 10, 2)");
            execute(connection, "INSERT INTO task_contents (id, user_task_id, version) VALUES (2, 10, 2)");
        }

        assertMigrationFailure(databasePath, "uk_task_contents_user_task_id_version", "doppelte task_contents-Versionen");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyTaskViewsContainDuplicateTemplatePaths() throws Exception {
        Path databasePath = tempDir.resolve("duplicate-task-view-template-paths.db");
        try (Connection connection = openConnection(databasePath)) {
            execute(connection, "CREATE TABLE task_views (id integer primary key, template_path varchar(255) not null)");
            execute(connection, "INSERT INTO task_views (id, template_path) VALUES (1, 'taskviews/html-css-editor')");
            execute(connection, "INSERT INTO task_views (id, template_path) VALUES (2, 'taskviews/html-css-editor')");
        }

        assertMigrationFailure(databasePath, "uk_task_views_template_path", "doppelte task_views.template_path-Werte");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyTaskViewReferencesConflict() throws Exception {
        Path databasePath = tempDir.resolve("conflicting-task-view-ids.db");
        try (Connection connection = openConnection(databasePath)) {
            execute(connection, "CREATE TABLE tasks (id integer primary key, task_view_id bigint, view_type_id bigint)");
            execute(connection, "INSERT INTO tasks (id, task_view_id, view_type_id) VALUES (1, 4, 7)");
        }

        assertMigrationFailure(databasePath, "task_view_id", "view_type_id");
    }

    @Test
    void localProfile_blocksStartupWhenLegacySubmissionsAreInconsistent() throws Exception {
        Path databasePath = tempDir.resolve("inconsistent-submissions.db");
        try (Connection connection = openConnection(databasePath)) {
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
        }

        assertMigrationFailure(databasePath, "submissions", "task_contents");
    }

    @Test
    void localProfile_blocksStartupWhenLegacySubmissionsAreNotMirroredIntoTaskContents() throws Exception {
        Path databasePath = tempDir.resolve("submissions-without-task-content-rows.db");
        try (Connection connection = openConnection(databasePath)) {
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
                "INSERT INTO submissions (id, submitted_at, version, task_content_id, user_task_id) VALUES (1, 123456, 2, 10, 3)");
        }

        assertMigrationFailure(databasePath, "submissions", "task_contents");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyUserTasksContainOrphanTaskReferences() throws Exception {
        Path databasePath = tempDir.resolve("orphan-user-task-task-id.db");
        try (Connection connection = openConnection(databasePath)) {
            createUsersTable(connection);
            createTaskStatusesTable(connection);
            createTasksTable(connection);
            createUserTasksTable(connection);

            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'user-1')");
            execute(connection,
                "INSERT INTO task_statuses (id, is_active, name) VALUES (1, 1, 'IN_BEARBEITUNG')");
            execute(connection,
                "INSERT INTO tasks (id, created_at, is_active, title, created_by_id) VALUES (1, 123456, 1, 'Task', 1)");
            execute(connection,
                "INSERT INTO user_tasks (id, last_modified, status_id, task_id, user_id) VALUES (20, 123456, 1, 999, 1)");
        }

        assertMigrationFailure(databasePath, "user_tasks.task_id", "verwaiste Referenzen");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyTaskReviewsContainOrphanReviewerReferences() throws Exception {
        Path databasePath = tempDir.resolve("orphan-task-review-reviewer-id.db");
        try (Connection connection = openConnection(databasePath)) {
            createUsersTable(connection);
            createTaskStatusesTable(connection);
            createTasksTable(connection);
            createUserTasksTable(connection);
            createTaskReviewsTable(connection);

            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'user-1')");
            execute(connection,
                "INSERT INTO task_statuses (id, is_active, name) VALUES (1, 1, 'IN_BEARBEITUNG')");
            execute(connection,
                "INSERT INTO tasks (id, created_at, is_active, title, created_by_id) VALUES (1, 123456, 1, 'Task', 1)");
            execute(connection,
                "INSERT INTO user_tasks (id, last_modified, status_id, task_id, user_id) VALUES (20, 123456, 1, 1, 1)");
            execute(connection, """
                INSERT INTO task_reviews (id, reviewed_at, version, reviewer_id, status_id, user_task_id)
                VALUES (40, 123456, 1, 999, 1, 20)
                """);
        }

        assertMigrationFailure(databasePath, "task_reviews.reviewer_id", "verwaiste Referenzen");
    }

    @Test
    void localProfile_blocksStartupWhenLegacyTaskGroupsContainOrphanTaskReferences() throws Exception {
        Path databasePath = tempDir.resolve("orphan-task-group-task-id.db");
        try (Connection connection = openConnection(databasePath)) {
            createUsersTable(connection);
            createTasksTable(connection);
            createGroupsTable(connection);
            createTaskGroupsTable(connection);

            execute(connection, "INSERT INTO users (id, created_at, open_id_subject) VALUES (1, 123456, 'user-1')");
            execute(connection,
                "INSERT INTO tasks (id, created_at, is_active, title, created_by_id) VALUES (1, 123456, 1, 'Task', 1)");
            execute(connection, "INSERT INTO groups (id, name) VALUES (1, 'A')");
            execute(connection, "INSERT INTO task_groups (task_id, group_id) VALUES (999, 1)");
        }

        assertMigrationFailure(databasePath, "task_groups.task_id", "verwaiste Referenzen");
    }

    private void assertMigrationFailure(Path databasePath, String... expectedMessageFragments) {
        Throwable failure = catchThrowable(() -> {
            try (ConfigurableApplicationContext ignored = runApplication(databasePath)) {
                // Startup must fail in Flyway before the context becomes usable.
            }
        });

        assertThat(failure).isNotNull();
        assertThat(failure).hasStackTraceContaining("Flyway migration blocked");

        Throwable rootCause = rootCauseOf(failure);
        assertThat(rootCause).isInstanceOf(SQLException.class);
        for (String expectedMessageFragment : expectedMessageFragments) {
            assertThat(rootCause).hasMessageContaining(expectedMessageFragment);
        }
    }

    private ConfigurableApplicationContext runApplication(Path databasePath) {
        return new SpringApplicationBuilder(FlywayMigrationTestApplication.class)
            .profiles("local")
            .web(WebApplicationType.NONE)
            .run(
                "--spring.datasource.url=jdbc:sqlite:" + databasePath,
                "--spring.main.banner-mode=off",
                "--spring.main.register-shutdown-hook=false",
                "--spring.devtools.restart.enabled=false",
                "--logging.level.root=WARN"
            );
    }

    private Connection openConnection(Path databasePath) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    }

    private void createUsersTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE users (
                id integer primary key,
                created_at timestamp not null,
                email varchar(255),
                family_name varchar(255),
                given_name varchar(255),
                last_login timestamp,
                name varchar(255),
                open_id_subject varchar(255) not null unique,
                preferred_username varchar(255)
            )
            """);
    }

    private void createTaskStatusesTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE task_statuses (
                id integer primary key,
                description varchar(255),
                is_active boolean not null,
                name varchar(255) not null unique,
                status_order integer
            )
            """);
    }

    private void createTasksTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE tasks (
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
            )
            """);
    }

    private void createUserTasksTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE user_tasks (
                id integer primary key,
                last_modified timestamp not null,
                started_at timestamp,
                status_id bigint not null,
                task_id bigint not null,
                user_id bigint not null
            )
            """);
    }

    private void createTaskReviewsTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE task_reviews (
                id integer primary key,
                comment TEXT,
                reviewed_at timestamp not null,
                version integer,
                reviewer_id bigint not null,
                status_id bigint not null,
                user_task_id bigint not null
            )
            """);
    }

    private void createGroupsTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE groups (
                id integer primary key,
                description varchar(255),
                name varchar(255) not null unique
            )
            """);
    }

    private void createTaskGroupsTable(Connection connection) throws SQLException {
        execute(connection, """
            CREATE TABLE task_groups (
                task_id bigint not null,
                group_id bigint not null,
                primary key (task_id, group_id)
            )
            """);
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private Throwable rootCauseOf(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
    })
    static class FlywayMigrationTestApplication {
    }
}
