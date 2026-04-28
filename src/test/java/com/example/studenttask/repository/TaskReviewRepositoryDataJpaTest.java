package com.example.studenttask.repository;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:task-review-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskReviewRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskReviewRepository taskReviewRepository;

    @Test
    void findByUserTaskOrderByReviewedAtDesc_returnsReviewHistoryInDescendingOrder() {
        UserTask userTask = persistUserTask("review-history");
        User reviewer = userRepository.save(new User("reviewer-history", "Reviewer", "reviewer-history@example.invalid"));
        TaskStatus complete = taskStatusRepository.save(new TaskStatus("VOLLSTAENDIG-REVIEW-HISTORY", "Complete", 2));
        TaskStatus needsRework = taskStatusRepository.save(new TaskStatus("UEBERARBEITUNG-REVIEW-HISTORY", "Needs rework", 3));

        TaskReview olderReview = review(userTask, reviewer, needsRework, 1, LocalDateTime.of(2026, 4, 20, 9, 0));
        TaskReview newerReview = review(userTask, reviewer, complete, 2, LocalDateTime.of(2026, 4, 22, 10, 30));

        taskReviewRepository.saveAndFlush(olderReview);
        taskReviewRepository.saveAndFlush(newerReview);

        assertThat(taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask))
            .extracting(TaskReview::getVersion, taskReview -> taskReview.getStatus().getName())
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple(2, "VOLLSTAENDIG-REVIEW-HISTORY"),
                org.assertj.core.groups.Tuple.tuple(1, "UEBERARBEITUNG-REVIEW-HISTORY")
            );
    }

    @Test
    void findByReviewerOrderByReviewedAtDesc_returnsReviewerHistoryInDescendingOrder() {
        UserTask firstUserTask = persistUserTask("reviewer-history-1");
        UserTask secondUserTask = persistUserTask("reviewer-history-2");
        User reviewer = userRepository.save(new User("reviewer-order", "Reviewer", "reviewer-order@example.invalid"));
        TaskStatus reviewStatus = taskStatusRepository.save(new TaskStatus("VOLLSTAENDIG-REVIEWER-ORDER", "Complete", 2));

        TaskReview olderReview = review(firstUserTask, reviewer, reviewStatus, 1, LocalDateTime.of(2026, 4, 18, 8, 15));
        olderReview.setComment("older");
        TaskReview newerReview = review(secondUserTask, reviewer, reviewStatus, 3, LocalDateTime.of(2026, 4, 23, 14, 45));
        newerReview.setComment("newer");

        taskReviewRepository.saveAndFlush(olderReview);
        taskReviewRepository.saveAndFlush(newerReview);

        assertThat(taskReviewRepository.findByReviewerOrderByReviewedAtDesc(reviewer))
            .extracting(TaskReview::getComment)
            .containsExactly("newer", "older");
    }

    private UserTask persistUserTask(String suffix) {
        User teacher = userRepository.save(new User("teacher-" + suffix, "Teacher", suffix + "-teacher@example.invalid"));
        User student = userRepository.save(new User("student-" + suffix, "Student", suffix + "@example.invalid"));
        TaskStatus status = taskStatusRepository.save(new TaskStatus("IN_BEARBEITUNG-" + suffix, "In Bearbeitung", 1));

        Task task = new Task();
        task.setTitle("Aufgabe " + suffix);
        task.setCreatedBy(teacher);
        task = taskRepository.save(task);

        return userTaskRepository.saveAndFlush(new UserTask(student, task, status));
    }

    private TaskReview review(UserTask userTask, User reviewer, TaskStatus status, Integer version,
            LocalDateTime reviewedAt) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);
        review.setStatus(status);
        review.setVersion(version);
        review.setReviewedAt(reviewedAt);
        return review;
    }
}
