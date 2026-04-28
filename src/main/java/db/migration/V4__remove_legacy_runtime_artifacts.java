package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class V4__remove_legacy_runtime_artifacts extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        migrateLegacyTaskViewReference(connection);
        dropLegacySubmissions(connection);
    }

    private void migrateLegacyTaskViewReference(Connection connection) throws SQLException {
        if (!tableExists(connection, "tasks") || !columnExists(connection, "tasks", "view_type_id")) {
            return;
        }

        if (!columnExists(connection, "tasks", "task_view_id")) {
            execute(connection, "ALTER TABLE tasks ADD COLUMN task_view_id bigint");
        }

        if (hasResult(connection,
            "SELECT 1 FROM tasks "
                + "WHERE task_view_id IS NOT NULL AND view_type_id IS NOT NULL AND task_view_id != view_type_id "
                + "LIMIT 1")) {
            throw new SQLException(
                "Flyway migration blocked: tasks.task_view_id und tasks.view_type_id enthalten widerspruechliche Werte."
            );
        }

        execute(connection,
            "UPDATE tasks SET task_view_id = view_type_id WHERE task_view_id IS NULL AND view_type_id IS NOT NULL");
        execute(connection, "ALTER TABLE tasks DROP COLUMN view_type_id");
    }

    private void dropLegacySubmissions(Connection connection) throws SQLException {
        if (!tableExists(connection, "submissions")) {
            return;
        }

        if (!tableExists(connection, "task_contents")) {
            throw new SQLException(
                "Flyway migration blocked: submissions existiert, aber task_contents fehlt fuer die Konsistenzpruefung."
            );
        }

        if (hasResult(connection, """
            SELECT 1
            FROM submissions s
            LEFT JOIN task_contents tc ON tc.id = s.task_content_id
            WHERE tc.id IS NULL
               OR tc.user_task_id != s.user_task_id
               OR tc.version != s.version
               OR COALESCE(tc.is_submitted, 0) != 1
            LIMIT 1
            """)) {
            throw new SQLException(
                "Flyway migration blocked: submissions enthaelt Legacy-Daten, die nicht konsistent in task_contents gespiegelt sind."
            );
        }

        execute(connection, "DROP TABLE submissions");
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

    private boolean hasResult(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next();
        }
    }
}
