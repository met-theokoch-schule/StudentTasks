package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.Submission;
import com.example.studenttask.repository.TaskReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class TaskReviewService {

    private final TaskReviewRepository taskReviewRepository;
    private final UserTaskService userTaskService;
    private final TaskStatusService taskStatusService;
    private final SubmissionService submissionService;

    public TaskReviewService(TaskReviewRepository taskReviewRepository,
                           UserTaskService userTaskService,
                           TaskStatusService taskStatusService,
                           SubmissionService submissionService) {
        this.taskReviewRepository = taskReviewRepository;
        this.userTaskService = userTaskService;
        this.taskStatusService = taskStatusService;
        this.submissionService = submissionService;
    }

    /**
     * Find all reviews for a user task ordered by review date descending
     */
    public List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask) {
        return taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Save a task review
     */
    public TaskReview save(TaskReview taskReview) {
        return taskReviewRepository.save(taskReview);
    }

    /**
     * Find review by id
     */
    public Optional<TaskReview> findById(Long id) {
        return taskReviewRepository.findById(id);
    }

    /**
     * Find all reviews by a specific reviewer
     */
    public List<TaskReview> findByReviewer(com.example.studenttask.model.User reviewer) {
        return taskReviewRepository.findByReviewerOrderByReviewedAtDesc(reviewer);
    }

    /**
     * Find the latest review for a user task
     */
    public Optional<TaskReview> findLatestReviewForUserTask(UserTask userTask) {
        return taskReviewRepository.findFirstByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Find all reviews for a user task
     */
    public List<TaskReview> findByUserTask(UserTask userTask) {
        return taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Create a new review for a user task
     */
    public TaskReview createReview(UserTask userTask, User reviewer, Long statusId, String comment, Long submissionId) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());

        // Set status if provided
        if (statusId != null) {
            taskStatusService.findById(statusId).ifPresent(review::setStatus);
            // Update the user task status as well
            taskStatusService.findById(statusId).ifPresent(userTask::setStatus);
        }

        // Set submission reference if provided (for version-specific reviews)
        if (submissionId != null && submissionId > 0) {
            submissionService.findById(submissionId).ifPresent(review::setSubmission);
        }
        // If no submissionId provided, it remains null (general review)

        return save(review);
    }

    /**
     * Create a new review for a user task with version
     */
    public TaskReview createReview(UserTask userTask, User reviewer, Long statusId, String comment, Long submissionId, Integer currentVersion) {
        System.out.println("=== DEBUG createReview ===");
        System.out.println("UserTask ID: " + userTask.getId());
        System.out.println("Reviewer: " + reviewer.getName());
        System.out.println("Status ID: " + statusId);
        System.out.println("Comment: " + comment);
        System.out.println("Current Version: " + currentVersion);

        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);

        TaskStatus status = taskStatusService.findById(statusId)
            .orElseThrow(() -> new RuntimeException("Status not found"));
        review.setStatus(status);
        System.out.println("Status gefunden: " + status.getName());

        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());

        // Speichere einfach die Versionsnummer
        if (currentVersion != null && currentVersion > 0) {
            review.setVersion(currentVersion);
            System.out.println("Review bezieht sich auf Version: " + currentVersion);
        } else {
            System.out.println("Kein Review für spezifische Version");
        }

        TaskReview savedReview = save(review);
        System.out.println("Review gespeichert mit ID: " + savedReview.getId() + ", Version: " + savedReview.getVersion());
        System.out.println("=== END DEBUG createReview ===");

        return savedReview;
    }

    /**
     * Get available status options for teacher reviews
     */
    public List<TaskStatus> getTeacherReviewStatuses() {
        List<TaskStatus> teacherStatuses = new ArrayList<>();

        // Nur diese beiden Status sind für Lehrer-Reviews sinnvoll
        taskStatusService.findByName("VOLLSTÄNDIG").ifPresent(teacherStatuses::add);
        taskStatusService.findByName("ÜBERARBEITUNG_NÖTIG").ifPresent(teacherStatuses::add);

        return teacherStatuses;
    }
}