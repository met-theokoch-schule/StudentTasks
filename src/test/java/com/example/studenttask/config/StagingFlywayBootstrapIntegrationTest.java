package com.example.studenttask.config;

import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import com.example.studenttask.repository.UnitTitleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
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
@ActiveProfiles("staging")
class StagingFlywayBootstrapIntegrationTest {

    private static final Path DATABASE_PATH = FlywayTestDatabaseSupport.createEmptyDatabasePath(
        "student-task-staging-flyway-");

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

    @Autowired
    private Environment environment;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:" + DATABASE_PATH);
        registry.add("spring.security.oauth2.client.registration.iserv.client-id", () -> "test-id");
        registry.add("spring.security.oauth2.client.registration.iserv.client-secret", () -> "test-secret");
        registry.add("spring.security.oauth2.client.registration.iserv.redirect-uri",
            () -> "https://staging.example.invalid/login/oauth2/code/iserv");
    }

    @AfterAll
    static void deleteDatabaseFile() throws IOException {
        FlywayTestDatabaseSupport.deleteDatabaseFile(DATABASE_PATH);
    }

    @Test
    void stagingProfile_bootstrapsEmptyDatabaseViaFlywayAndLoadsStagingSettings() {
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

        assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("none");
        assertThat(environment.getProperty("spring.flyway.enabled")).isEqualTo("true");
        assertThat(environment.getProperty("spring.flyway.baseline-on-migrate")).isEqualTo("true");
        assertThat(environment.getProperty("spring.flyway.clean-disabled")).isEqualTo("true");
        assertThat(environment.getProperty("server.address")).isEqualTo("0.0.0.0");
        assertThat(environment.getProperty("server.servlet.context-path")).isEqualTo("/");
        assertThat(environment.getProperty("server.servlet.session.cookie.same-site")).isEqualTo("none");
        assertThat(environment.getProperty("server.servlet.session.cookie.secure")).isEqualTo("true");

        Integer roleId = jdbcTemplate.queryForObject("SELECT id FROM roles ORDER BY id LIMIT 1", Integer.class);
        assertThat(roleId).isNotNull();
        assertThatThrownBy(() ->
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", 999999L, roleId))
            .isInstanceOf(DataAccessException.class);
    }
}
