package com.example.studenttask.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

final class FlywayTestDatabaseSupport {

    private FlywayTestDatabaseSupport() {
    }

    static Path createEmptyDatabasePath(String prefix) {
        try {
            Path databasePath = Files.createTempFile(prefix, ".db");
            Files.deleteIfExists(databasePath);
            return databasePath;
        } catch (IOException exception) {
            throw new IllegalStateException("Konnte temporaere SQLite-Datei fuer Flyway-Test nicht anlegen", exception);
        }
    }

    static Path createLegacyDatabase(String prefix) {
        Path databasePath = createEmptyDatabasePath(prefix);
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE task_views (
                    id integer primary key,
                    description varchar(1000),
                    is_active boolean not null,
                    name varchar(255) not null,
                    template_path varchar(255) not null
                )
                """);
            statement.execute("""
                CREATE TABLE task_statuses (
                    id integer primary key,
                    description varchar(255),
                    is_active boolean not null,
                    name varchar(255) not null unique,
                    status_order integer
                )
                """);
            statement.execute("""
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
            statement.execute("""
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
                    unit_title_id varchar(255),
                    view_type_id bigint
                )
                """);
            statement.execute("""
                CREATE TABLE user_tasks (
                    id integer primary key,
                    last_modified timestamp not null,
                    started_at timestamp,
                    status_id bigint not null,
                    task_id bigint not null,
                    user_id bigint not null
                )
                """);
            statement.execute("""
                CREATE TABLE task_contents (
                    id integer primary key,
                    content TEXT,
                    is_submitted boolean not null,
                    saved_at timestamp not null,
                    version integer not null,
                    user_task_id bigint not null
                )
                """);
            statement.execute("""
                CREATE TABLE submissions (
                    id integer primary key,
                    submitted_at timestamp not null,
                    version integer not null,
                    task_content_id bigint not null,
                    user_task_id bigint not null
                )
                """);
            statement.execute("""
                INSERT INTO task_statuses (id, description, is_active, name, status_order)
                VALUES
                    (1, 'Aufgabe wurde noch nicht begonnen', 1, 'NICHT_BEGONNEN', 1),
                    (2, 'Aufgabe wird bearbeitet', 1, 'IN_BEARBEITUNG', 2),
                    (3, 'Aufgabe wurde abgegeben', 1, 'ABGEGEBEN', 3),
                    (4, 'Aufgabe muss überarbeitet werden', 1, 'ÜBERARBEITUNG_NÖTIG', 4),
                    (5, 'Aufgabe ist vollständig abgeschlossen', 1, 'VOLLSTÄNDIG', 5)
                """);
            statement.execute("""
                INSERT INTO users (id, created_at, email, name, open_id_subject)
                VALUES
                    (1, 1754597600000, 'teacher@example.invalid', 'Teacher', 'teacher-sub'),
                    (2, 1754597601000, 'student@example.invalid', 'Student', 'student-sub')
                """);
            statement.execute("""
                INSERT INTO task_views (id, description, is_active, name, template_path)
                VALUES (1, 'legacy', 1, 'HTML+CSS Editor', 'taskviews/html-css-editor')
                """);
            statement.execute("""
                INSERT INTO tasks (
                    id, created_at, default_submission, description, due_date, is_active, title,
                    tutorial, created_by_id, task_view_id, unit_title_id, view_type_id
                ) VALUES (
                    1, 1754597690000, NULL, 'legacy task', NULL, 1, 'Legacy Aufgabe', NULL, 1, NULL, NULL, 1
                )
                """);
            statement.execute("""
                INSERT INTO user_tasks (id, last_modified, started_at, status_id, task_id, user_id)
                VALUES (3, 1754597700000, 1754597695000, 3, 1, 2)
                """);
            statement.execute("""
                INSERT INTO task_contents (id, content, is_submitted, saved_at, version, user_task_id)
                VALUES (10, '{}', 1, 1754597700875, 2, 3)
                """);
            statement.execute("""
                INSERT INTO submissions (id, submitted_at, version, task_content_id, user_task_id)
                VALUES (1, 1754597700878, 2, 10, 3)
                """);
            return databasePath;
        } catch (Exception exception) {
            throw new IllegalStateException("Konnte Legacy-Schema fuer Flyway-Test nicht vorbereiten", exception);
        }
    }

    static void deleteDatabaseFile(Path databasePath) throws IOException {
        Files.deleteIfExists(databasePath);
    }
}
