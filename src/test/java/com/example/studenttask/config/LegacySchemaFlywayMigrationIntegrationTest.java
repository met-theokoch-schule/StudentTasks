package com.example.studenttask.config;

import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import com.example.studenttask.repository.UnitTitleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
class LegacySchemaFlywayMigrationIntegrationTest {

    private static final Path DATABASE_PATH = FlywayTestDatabaseSupport.createLegacyDatabase(
        "student-task-legacy-flyway-");

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskViewRepository taskViewRepository;

    @Autowired
    private UnitTitleRepository unitTitleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:" + DATABASE_PATH);
        registry.add("spring.security.oauth2.client.registration.iserv.client-id", () -> "test-id");
        registry.add("spring.security.oauth2.client.registration.iserv.client-secret", () -> "test-secret");
        registry.add("spring.security.oauth2.client.registration.iserv.redirect-uri",
            () -> "http://localhost/login/oauth2/code/iserv");
    }

    @AfterAll
    static void deleteDatabaseFile() throws IOException {
        FlywayTestDatabaseSupport.deleteDatabaseFile(DATABASE_PATH);
    }

    @Test
    void localProfile_migratesLegacyTaskViewsSchemaAndSeedsReferenceData() {
        assertThat(roleRepository.count()).isEqualTo(3);
        assertThat(taskStatusRepository.count()).isEqualTo(5);
        assertThat(taskViewRepository.count()).isGreaterThan(0);
        assertThat(unitTitleRepository.count()).isGreaterThan(0);
        assertThat(jdbcTemplate.queryForObject("PRAGMA foreign_keys", Integer.class)).isEqualTo(1);

        assertThat(tableExists("submissions")).isFalse();
        assertThat(columnExists("task_views", "submit_marks_complete")).isTrue();
        assertThat(columnExists("tasks", "view_type_id")).isFalse();
        assertThat(foreignKeyExists("tasks", "task_view_id", "task_views", "id")).isTrue();
        assertThat(foreignKeyExists("task_contents", "user_task_id", "user_tasks", "id")).isTrue();
        assertThat(jdbcTemplate.queryForObject(
            "SELECT submit_marks_complete FROM task_views WHERE template_path = ?",
            Integer.class,
            "taskviews/html-css-editor"
        )).isEqualTo(0);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT task_view_id FROM tasks WHERE id = ?",
            Integer.class,
            1
        )).isEqualTo(1);

        TaskView autoCompleteView = taskViewRepository.findByTemplatePath("taskviews/code-mainia-python");
        assertThat(autoCompleteView).isNotNull();
        assertThat(autoCompleteView.getSubmitMarksComplete()).isTrue();

        List<String> appliedVersions = jdbcTemplate.query(
            "SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank",
            (resultSet, rowNum) -> resultSet.getString("version")
        );
        assertThat(appliedVersions).containsExactly("0", "1", "2", "3", "4", "5");
    }

    private boolean columnExists(String tableName, String columnName) {
        return jdbcTemplate.query("PRAGMA table_info(" + tableName + ")", resultSet -> {
            while (resultSet.next()) {
                if (columnName.equalsIgnoreCase(resultSet.getString("name"))) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean tableExists(String tableName) {
        Integer matchCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = ?",
            Integer.class,
            tableName
        );
        return matchCount != null && matchCount > 0;
    }

    private boolean foreignKeyExists(String tableName, String fromColumn, String targetTable, String targetColumn) {
        return jdbcTemplate.query("PRAGMA foreign_key_list(" + tableName + ")", resultSet -> {
            while (resultSet.next()) {
                if (fromColumn.equalsIgnoreCase(resultSet.getString("from"))
                    && targetTable.equalsIgnoreCase(resultSet.getString("table"))
                    && targetColumn.equalsIgnoreCase(resultSet.getString("to"))) {
                    return true;
                }
            }
            return false;
        });
    }

}
