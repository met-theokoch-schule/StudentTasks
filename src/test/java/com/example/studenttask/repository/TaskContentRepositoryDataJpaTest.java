package com.example.studenttask.repository;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:task-content-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskContentRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskContentRepository taskContentRepository;

    @Test
    void findTopByUserTaskOrderByVersionDesc_returnsLatestVersion() {
        UserTask userTask = persistUserTask("student-1");

        taskContentRepository.saveAndFlush(taskContent(userTask, 1, "erste Version"));
        TaskContent latestContent = taskContentRepository.saveAndFlush(taskContent(userTask, 2, "zweite Version"));

        assertThat(taskContentRepository.findTopByUserTaskOrderByVersionDesc(userTask))
            .contains(latestContent);
    }

    @Test
    void findByUserTaskAndVersion_returnsMatchingVersion() {
        UserTask userTask = persistUserTask("student-2");
        taskContentRepository.saveAndFlush(taskContent(userTask, 1, "v1"));
        taskContentRepository.saveAndFlush(taskContent(userTask, 2, "v2"));

        TaskContent content = taskContentRepository.findByUserTaskAndVersion(userTask, 2);

        assertThat(content).isNotNull();
        assertThat(content.getVersion()).isEqualTo(2);
        assertThat(content.getContent()).isEqualTo("v2");
        assertThat(taskContentRepository.existsByUserTaskAndVersion(userTask, 1)).isTrue();
    }

    private UserTask persistUserTask(String studentSubject) {
        User teacher = userRepository.save(new User("teacher-" + studentSubject, "Teacher", studentSubject + "-teacher@example.invalid"));
        User student = userRepository.save(new User(studentSubject, "Student", studentSubject + "@example.invalid"));
        TaskStatus status = taskStatusRepository.save(new TaskStatus("IN_BEARBEITUNG-" + studentSubject, "In Bearbeitung", 1));

        Task task = new Task();
        task.setTitle("Aufgabe " + studentSubject);
        task.setCreatedBy(teacher);
        task = taskRepository.save(task);

        return userTaskRepository.saveAndFlush(new UserTask(student, task, status));
    }

    private TaskContent taskContent(UserTask userTask, int version, String content) {
        TaskContent taskContent = new TaskContent();
        taskContent.setUserTask(userTask);
        taskContent.setVersion(version);
        taskContent.setContent(content);
        return taskContent;
    }
}
