package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class V1__add_runtime_unique_constraints extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        ensureUniqueIndex(
            connection,
            "user_tasks",
            "uk_user_tasks_user_id_task_id",
            "user_id, task_id",
            "SELECT 1 FROM user_tasks GROUP BY user_id, task_id HAVING COUNT(*) > 1 LIMIT 1",
            "doppelte user_tasks-Zuordnungen fuer (user_id, task_id)"
        );

        ensureUniqueIndex(
            connection,
            "task_contents",
            "uk_task_contents_user_task_id_version",
            "user_task_id, version",
            "SELECT 1 FROM task_contents GROUP BY user_task_id, version HAVING COUNT(*) > 1 LIMIT 1",
            "doppelte task_contents-Versionen fuer (user_task_id, version)"
        );

        ensureUniqueIndex(
            connection,
            "task_views",
            "uk_task_views_template_path",
            "template_path",
            "SELECT 1 FROM task_views GROUP BY template_path HAVING COUNT(*) > 1 LIMIT 1",
            "doppelte task_views.template_path-Werte"
        );
    }

    private void ensureUniqueIndex(
            Connection connection,
            String tableName,
            String indexName,
            String columns,
            String duplicateCheckSql,
            String duplicateDescription) throws SQLException {
        if (!tableExists(connection, tableName) || indexExists(connection, indexName)) {
            return;
        }

        if (hasDuplicateRows(connection, duplicateCheckSql)) {
            throw new SQLException(
                "Flyway migration blocked: Index " + indexName + " kann nicht angelegt werden, weil "
                    + duplicateDescription + " existieren."
            );
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE UNIQUE INDEX " + indexName + " ON " + tableName + " (" + columns + ")");
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        return sqliteMasterEntryExists(connection, "table", tableName);
    }

    private boolean indexExists(Connection connection, String indexName) throws SQLException {
        return sqliteMasterEntryExists(connection, "index", indexName);
    }

    private boolean sqliteMasterEntryExists(Connection connection, String type, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM sqlite_master WHERE type = ? AND name = ? LIMIT 1")) {
            statement.setString(1, type);
            statement.setString(2, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean hasDuplicateRows(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next();
        }
    }
}
