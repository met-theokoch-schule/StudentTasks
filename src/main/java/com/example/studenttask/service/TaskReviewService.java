package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.TaskReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import com.example.studenttask.model.Submission;

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
        review.setReviewedAt(ZonedDateTime.now());

        // Set status if provided
        if (statusId != null) {
            TaskStatus status = taskStatusService.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found: " + statusId));
            review.setStatus(status);
             // Update the user task status as well
            taskStatusService.findById(statusId).ifPresent(userTask::setStatus);
        }

        // Set submission if provided
        if (submissionId != null && submissionId > 0) {
            // Find the submission by ID
            Optional<Submission> submissionOpt = submissionService.findById(submissionId);
            if (submissionOpt.isPresent()) {
                review.setSubmission(submissionOpt.get());
                System.out.println("🔗 Review verknüpft mit Submission ID: " + submissionId + ", Version: " + submissionOpt.get().getVersion());
            } else {
                System.out.println("⚠️ Submission mit ID " + submissionId + " nicht gefunden!");
            }
        } else {
            System.out.println("ℹ️ Kein submissionId angegeben oder submissionId ist 0/null");
        }

        TaskReview savedReview = save(review);
        System.out.println("✅ Review gespeichert mit ID: " + savedReview.getId() + ", submission_id: " + (savedReview.getSubmission() != null ? savedReview.getSubmission().getId() : "null"));

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