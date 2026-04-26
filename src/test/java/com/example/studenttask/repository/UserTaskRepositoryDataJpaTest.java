package com.example.studenttask.repository;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:user-task-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserTaskRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Test
    void findByUserAndTask_returnsPersistedAssignment() {
        User teacher = userRepository.save(new User("teacher-sub", "Teacher", "teacher@example.invalid"));
        User student = userRepository.save(new User("student-sub", "Student", "student@example.invalid"));
        TaskStatus status = taskStatusRepository.save(new TaskStatus("IN_BEARBEITUNG", "In Bearbeitung", 1));

        Task task = new Task();
        task.setTitle("Arbeitsblatt");
        task.setCreatedBy(teacher);
        Task persistedTask = taskRepository.save(task);

        UserTask persistedUserTask = userTaskRepository.saveAndFlush(new UserTask(student, persistedTask, status));

        assertThat(userTaskRepository.findByUserAndTask(student, persistedTask))
            .contains(persistedUserTask);
    }
}
