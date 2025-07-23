package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.Submission;
import com.example.studenttask.repository.TaskReviewRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class TaskReviewService {

    private final TaskReviewRepository taskReviewRepository;
    private final UserTaskRepository userTaskRepository;
    private final TaskStatusService taskStatusService;
    private final SubmissionService submissionService;

    public TaskReviewService(TaskReviewRepository taskReviewRepository,
                           UserTaskRepository userTaskRepository,
                           TaskStatusService taskStatusService,
                           SubmissionService submissionService) {
        this.taskReviewRepository = taskReviewRepository;
        this.userTaskRepository = userTaskRepository;
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

    @Transactional
    public TaskReview createReview(UserTask userTask, User reviewer, Long statusId, String comment,
                                 Long submissionId, Integer version) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());
        review.setVersion(version);

        // Set status if provided
        if (statusId != null) {
            Optional<TaskStatus> statusOpt = taskStatusService.findById(statusId);
            if (statusOpt.isPresent()) {
                TaskStatus status = statusOpt.get();
                review.setStatus(status);
                // Update the user task status as well
                userTask.setStatus(status);
                userTask.setLastModified(LocalDateTime.now());

                // Save UserTask explicitly to ensure status change is persisted
                userTaskRepository.save(userTask);
            }
        }

        // Set submission reference if provided (for version-specific reviews)

        return save(review);
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
            Optional<TaskStatus> statusOpt = taskStatusService.findById(statusId);
            if (statusOpt.isPresent()) {
                TaskStatus status = statusOpt.get();
                review.setStatus(status);
                // Update the user task status as well
                userTask.setStatus(status);
                userTask.setLastModified(LocalDateTime.now());
            }
        }

        // Set submission reference if provided (for version-specific reviews)
        if (submissionId != null && submissionId > 0) {
            //submissionService.findById(submissionId).ifPresent(review::setSubmission);  //Removed setSubmission
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

        // Set version directly in review
        if (currentVersion != null && currentVersion > 0) {
            review.setVersion(currentVersion);
            System.out.println("Version direkt gesetzt: " + currentVersion);
        } else {
            System.out.println("Keine Version angegeben - Review ohne spezifische Version");
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

    public void createReview(UserTask userTask, TaskStatus status, String comment, Integer version, User reviewer) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setStatus(status);
        review.setComment(comment);
        review.setVersion(version);
        review.setReviewer(reviewer);
        review.setReviewedAt(LocalDateTime.now());

        taskReviewRepository.save(review);
    }

    public boolean hasReviewsForVersion(UserTask userTask, Integer version) {
        return taskReviewRepository.existsByUserTaskAndVersion(userTask, version);
    }
}