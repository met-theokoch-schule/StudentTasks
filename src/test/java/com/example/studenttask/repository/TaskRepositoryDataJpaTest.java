package com.example.studenttask.repository;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:task-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void findTasksForUserGroups_returnsDistinctActiveTasksOrderedByCreatedAtDesc() {
        User teacher = persistUser("teacher-task-groups");
        Group q2 = groupRepository.saveAndFlush(new Group("Q2", "Q2"));
        Group q3 = groupRepository.saveAndFlush(new Group("Q3", "Q3"));
        Group other = groupRepository.saveAndFlush(new Group("Other", "Other"));

        taskRepository.saveAndFlush(task(
            "Aktive Doppelgruppe",
            teacher,
            LocalDateTime.of(2026, 4, 25, 9, 0),
            null,
            true,
            Set.of(q2, q3)
        ));
        taskRepository.saveAndFlush(task(
            "Aktive Einzelgruppe",
            teacher,
            LocalDateTime.of(2026, 4, 24, 9, 0),
            null,
            true,
            Set.of(q2)
        ));
        taskRepository.saveAndFlush(task(
            "Inaktive Doppelgruppe",
            teacher,
            LocalDateTime.of(2026, 4, 26, 9, 0),
            null,
            false,
            Set.of(q2, q3)
        ));
        taskRepository.saveAndFlush(task(
            "Fremdgruppe",
            teacher,
            LocalDateTime.of(2026, 4, 27, 9, 0),
            null,
            true,
            Set.of(other)
        ));

        assertThat(taskRepository.findByAssignedGroupsIn(Set.of(q2, q3)))
            .extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Aktive Doppelgruppe", "Aktive Einzelgruppe", "Inaktive Doppelgruppe");

        assertThat(taskRepository.findTasksForUserGroups(Set.of(q2, q3)))
            .extracting(Task::getTitle)
            .containsExactly("Aktive Doppelgruppe", "Aktive Einzelgruppe");
    }

    @Test
    void findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc_returnsOnlyActiveCreatorTasksOrderedByCreatedAtDesc() {
        User teacher = persistUser("teacher-created-by");
        User otherTeacher = persistUser("teacher-other-created-by");

        taskRepository.saveAndFlush(task(
            "Neu aktiv",
            teacher,
            LocalDateTime.of(2026, 4, 27, 10, 0),
            null,
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Alt aktiv",
            teacher,
            LocalDateTime.of(2026, 4, 20, 10, 0),
            null,
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Inaktiv",
            teacher,
            LocalDateTime.of(2026, 4, 28, 10, 0),
            null,
            false,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Andere Lehrkraft",
            otherTeacher,
            LocalDateTime.of(2026, 4, 29, 10, 0),
            null,
            true,
            Set.of()
        ));

        assertThat(taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher))
            .extracting(Task::getTitle)
            .containsExactly("Neu aktiv", "Alt aktiv");
    }

    @Test
    void findByDueDateBeforeAndIsActiveOrderByDueDateAsc_returnsOnlyActiveOverdueTasksSortedAscending() {
        User teacher = persistUser("teacher-overdue");
        LocalDateTime cutoff = LocalDateTime.of(2026, 4, 27, 12, 0);

        taskRepository.saveAndFlush(task(
            "Frueh ueberfaellig",
            teacher,
            LocalDateTime.of(2026, 4, 20, 10, 0),
            LocalDateTime.of(2026, 4, 25, 8, 0),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Spaet ueberfaellig",
            teacher,
            LocalDateTime.of(2026, 4, 21, 10, 0),
            LocalDateTime.of(2026, 4, 26, 18, 0),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Zukuenftig",
            teacher,
            LocalDateTime.of(2026, 4, 22, 10, 0),
            LocalDateTime.of(2026, 4, 28, 8, 0),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Inaktiv ueberfaellig",
            teacher,
            LocalDateTime.of(2026, 4, 23, 10, 0),
            LocalDateTime.of(2026, 4, 24, 8, 0),
            false,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Ohne Due Date",
            teacher,
            LocalDateTime.of(2026, 4, 24, 10, 0),
            null,
            true,
            Set.of()
        ));

        assertThat(taskRepository.findByDueDateBeforeAndIsActiveOrderByDueDateAsc(cutoff, true))
            .extracting(Task::getTitle, Task::getDueDate)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("Frueh ueberfaellig", LocalDateTime.of(2026, 4, 25, 8, 0)),
                org.assertj.core.groups.Tuple.tuple("Spaet ueberfaellig", LocalDateTime.of(2026, 4, 26, 18, 0))
            );
    }

    @Test
    void findByDueDateBetweenAndIsActiveOrderByDueDateAsc_returnsOnlyActiveWindowTasksIncludingBoundaries() {
        User teacher = persistUser("teacher-window");
        LocalDateTime start = LocalDateTime.of(2026, 4, 27, 8, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 27, 18, 0);

        taskRepository.saveAndFlush(task(
            "Startgrenze",
            teacher,
            LocalDateTime.of(2026, 4, 20, 10, 0),
            start,
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Mittags",
            teacher,
            LocalDateTime.of(2026, 4, 21, 10, 0),
            LocalDateTime.of(2026, 4, 27, 12, 0),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Endgrenze",
            teacher,
            LocalDateTime.of(2026, 4, 22, 10, 0),
            end,
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Vorher",
            teacher,
            LocalDateTime.of(2026, 4, 23, 10, 0),
            LocalDateTime.of(2026, 4, 27, 7, 59),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Nachher",
            teacher,
            LocalDateTime.of(2026, 4, 24, 10, 0),
            LocalDateTime.of(2026, 4, 27, 18, 1),
            true,
            Set.of()
        ));
        taskRepository.saveAndFlush(task(
            "Inaktiv im Fenster",
            teacher,
            LocalDateTime.of(2026, 4, 25, 10, 0),
            LocalDateTime.of(2026, 4, 27, 14, 0),
            false,
            Set.of()
        ));

        assertThat(taskRepository.findByDueDateBetweenAndIsActiveOrderByDueDateAsc(start, end, true))
            .extracting(Task::getTitle, Task::getDueDate)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("Startgrenze", start),
                org.assertj.core.groups.Tuple.tuple("Mittags", LocalDateTime.of(2026, 4, 27, 12, 0)),
                org.assertj.core.groups.Tuple.tuple("Endgrenze", end)
            );
    }

    private User persistUser(String openIdSubject) {
        return userRepository.saveAndFlush(new User(openIdSubject, openIdSubject, openIdSubject + "@example.invalid"));
    }

    private Task task(
            String title,
            User createdBy,
            LocalDateTime createdAt,
            LocalDateTime dueDate,
            boolean active,
            Set<Group> assignedGroups) {
        Task task = new Task();
        task.setTitle(title);
        task.setCreatedBy(createdBy);
        task.setCreatedAt(createdAt);
        task.setDueDate(dueDate);
        task.setIsActive(active);
        task.setAssignedGroups(assignedGroups);
        return task;
    }
}
