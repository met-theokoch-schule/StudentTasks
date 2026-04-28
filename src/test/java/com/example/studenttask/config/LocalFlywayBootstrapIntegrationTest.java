package com.example.studenttask.config;

import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import com.example.studenttask.repository.UnitTitleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
class LocalFlywayBootstrapIntegrationTest {

    private static final Path DATABASE_PATH = FlywayTestDatabaseSupport.createEmptyDatabasePath(
        "student-task-local-flyway-");

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
    void localProfile_bootstrapsEmptyDatabaseViaFlywayAndSeedsReferenceData() {
        assertThat(roleRepository.count()).isEqualTo(3);
        assertThat(taskStatusRepository.count()).isEqualTo(5);
        assertThat(taskViewRepository.count()).isGreaterThan(0);
        assertThat(unitTitleRepository.count()).isGreaterThan(0);
        assertThat(jdbcTemplate.queryForObject("PRAGMA foreign_keys", Integer.class)).isEqualTo(1);

        List<String> appliedVersions = jdbcTemplate.query(
            "SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank",
            (resultSet, rowNum) -> resultSet.getString("version")
        );
        assertThat(appliedVersions).containsExactly("1", "2", "3", "4", "5");

        Integer roleId = jdbcTemplate.queryForObject("SELECT id FROM roles ORDER BY id LIMIT 1", Integer.class);
        assertThat(roleId).isNotNull();
        assertThatThrownBy(() ->
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", 999999L, roleId))
            .isInstanceOf(DataAccessException.class);
    }

}
