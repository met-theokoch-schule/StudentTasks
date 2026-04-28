package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class V5__add_sqlite_foreign_keys extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        validateReference(connection, "tasks", "users", "created_by_id", "id");
        validateReference(connection, "tasks", "task_views", "task_view_id", "id");
        validateReference(connection, "tasks", "unit_titles", "unit_title_id", "id");
        validateReference(connection, "user_tasks", "users", "user_id", "id");
        validateReference(connection, "user_tasks", "tasks", "task_id", "id");
        validateReference(connection, "user_tasks", "task_statuses", "status_id", "id");
        validateReference(connection, "task_contents", "user_tasks", "user_task_id", "id");
        validateReference(connection, "task_reviews", "user_tasks", "user_task_id", "id");
        validateReference(connection, "task_reviews", "users", "reviewer_id", "id");
        validateReference(connection, "task_reviews", "task_statuses", "status_id", "id");
        validateReference(connection, "user_groups", "users", "user_id", "id");
        validateReference(connection, "user_groups", "groups", "group_id", "id");
        validateReference(connection, "user_roles", "users", "user_id", "id");
        validateReference(connection, "user_roles", "roles", "role_id", "id");
        validateReference(connection, "task_groups", "tasks", "task_id", "id");
        validateReference(connection, "task_groups", "groups", "group_id", "id");

        rebuildTasks(connection);
        rebuildUserTasks(connection);
        rebuildTaskContents(connection);
        rebuildTaskReviews(connection);
        rebuildUserGroups(connection);
        rebuildUserRoles(connection);
        rebuildTaskGroups(connection);

        if (hasResult(connection, "PRAGMA foreign_key_check")) {
            throw new SQLException("Flyway migration blocked: PRAGMA foreign_key_check meldet verbleibende FK-Verletzungen.");
        }
    }

    private void rebuildTasks(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "tasks",
            """
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
                    FOREIGN KEY (created_by_id) REFERENCES users(id),
                    FOREIGN KEY (task_view_id) REFERENCES task_views(id),
                    FOREIGN KEY (unit_title_id) REFERENCES unit_titles(id)
                )
                """,
            """
                INSERT INTO tasks (
                    id, created_at, default_submission, description, due_date, is_active, title, tutorial,
                    created_by_id, task_view_id, unit_title_id
                )
                SELECT
                    id, created_at, default_submission, description, due_date, is_active, title, tutorial,
                    created_by_id, task_view_id, unit_title_id
                FROM tasks__old
                """
        );
    }

    private void rebuildUserTasks(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "user_tasks",
            """
                CREATE TABLE user_tasks (
                    id integer primary key,
                    last_modified timestamp not null,
                    started_at timestamp,
                    status_id bigint not null,
                    task_id bigint not null,
                    user_id bigint not null,
                    FOREIGN KEY (status_id) REFERENCES task_statuses(id),
                    FOREIGN KEY (task_id) REFERENCES tasks(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """,
            """
                INSERT INTO user_tasks (id, last_modified, started_at, status_id, task_id, user_id)
                SELECT id, last_modified, started_at, status_id, task_id, user_id
                FROM user_tasks__old
                """,
            "CREATE UNIQUE INDEX uk_user_tasks_user_id_task_id ON user_tasks (user_id, task_id)"
        );
    }

    private void rebuildTaskContents(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "task_contents",
            """
                CREATE TABLE task_contents (
                    id integer primary key,
                    content TEXT,
                    is_submitted boolean not null,
                    saved_at timestamp not null,
                    version integer not null,
                    user_task_id bigint not null,
                    FOREIGN KEY (user_task_id) REFERENCES user_tasks(id)
                )
                """,
            """
                INSERT INTO task_contents (id, content, is_submitted, saved_at, version, user_task_id)
                SELECT id, content, is_submitted, saved_at, version, user_task_id
                FROM task_contents__old
                """,
            "CREATE UNIQUE INDEX uk_task_contents_user_task_id_version ON task_contents (user_task_id, version)"
        );
    }

    private void rebuildTaskReviews(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "task_reviews",
            """
                CREATE TABLE task_reviews (
                    id integer primary key,
                    comment TEXT,
                    reviewed_at timestamp not null,
                    version integer,
                    reviewer_id bigint not null,
                    status_id bigint not null,
                    user_task_id bigint not null,
                    FOREIGN KEY (reviewer_id) REFERENCES users(id),
                    FOREIGN KEY (status_id) REFERENCES task_statuses(id),
                    FOREIGN KEY (user_task_id) REFERENCES user_tasks(id)
                )
                """,
            """
                INSERT INTO task_reviews (id, comment, reviewed_at, version, reviewer_id, status_id, user_task_id)
                SELECT id, comment, reviewed_at, version, reviewer_id, status_id, user_task_id
                FROM task_reviews__old
                """
        );
    }

    private void rebuildUserGroups(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "user_groups",
            """
                CREATE TABLE user_groups (
                    user_id bigint not null,
                    group_id bigint not null,
                    primary key (user_id, group_id),
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (group_id) REFERENCES groups(id)
                )
                """,
            "INSERT INTO user_groups (user_id, group_id) SELECT user_id, group_id FROM user_groups__old"
        );
    }

    private void rebuildUserRoles(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "user_roles",
            """
                CREATE TABLE user_roles (
                    user_id bigint not null,
                    role_id bigint not null,
                    primary key (user_id, role_id),
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (role_id) REFERENCES roles(id)
                )
                """,
            "INSERT INTO user_roles (user_id, role_id) SELECT user_id, role_id FROM user_roles__old"
        );
    }

    private void rebuildTaskGroups(Connection connection) throws SQLException {
        rebuildTable(
            connection,
            "task_groups",
            """
                CREATE TABLE task_groups (
                    task_id bigint not null,
                    group_id bigint not null,
                    primary key (task_id, group_id),
                    FOREIGN KEY (task_id) REFERENCES tasks(id),
                    FOREIGN KEY (group_id) REFERENCES groups(id)
                )
                """,
            "INSERT INTO task_groups (task_id, group_id) SELECT task_id, group_id FROM task_groups__old"
        );
    }

    private void rebuildTable(
            Connection connection,
            String tableName,
            String createSql,
            String copySql,
            String... postSqlStatements) throws SQLException {
        if (!tableExists(connection, tableName) || hasForeignKeys(connection, tableName)) {
            return;
        }

        String oldTableName = tableName + "__old";
        execute(connection, "DROP TABLE IF EXISTS " + oldTableName);
        execute(connection, "ALTER TABLE " + tableName + " RENAME TO " + oldTableName);
        execute(connection, createSql);
        execute(connection, copySql);
        execute(connection, "DROP TABLE " + oldTableName);
        for (String sql : postSqlStatements) {
            execute(connection, sql);
        }
    }

    private void validateReference(
            Connection connection,
            String childTable,
            String parentTable,
            String childColumn,
            String parentColumn) throws SQLException {
        if (!tableExists(connection, childTable)) {
            return;
        }
        if (!tableExists(connection, parentTable)) {
            throw new SQLException(
                "Flyway migration blocked: Tabelle " + parentTable + " fehlt fuer FK " + childTable + "." + childColumn
            );
        }
        if (!columnExists(connection, childTable, childColumn) || !columnExists(connection, parentTable, parentColumn)) {
            throw new SQLException(
                "Flyway migration blocked: FK-Spalten fuer " + childTable + "." + childColumn + " -> "
                    + parentTable + "." + parentColumn + " fehlen."
            );
        }

        String sql = "SELECT 1 FROM " + childTable + " c LEFT JOIN " + parentTable + " p ON p." + parentColumn
            + " = c." + childColumn
            + " WHERE c." + childColumn + " IS NOT NULL AND p." + parentColumn + " IS NULL LIMIT 1";
        if (hasResult(connection, sql)) {
            throw new SQLException(
                "Flyway migration blocked: verwaiste Referenzen in " + childTable + "." + childColumn
                    + " auf " + parentTable + "." + parentColumn + "."
            );
        }
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM sqlite_master WHERE type = ? AND name = ? LIMIT 1")) {
            statement.setString(1, "table");
            statement.setString(2, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
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

    private boolean hasForeignKeys(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_key_list(" + tableName + ")")) {
            return resultSet.next();
        }
    }

    private boolean hasResult(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next();
        }
    }
}
