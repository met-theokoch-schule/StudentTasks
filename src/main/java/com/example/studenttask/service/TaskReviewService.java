package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.model.Submission;
import com.example.studenttask.repository.TaskReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Service
public class TaskReviewService {

    @Autowired
    private TaskReviewRepository taskReviewRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private SubmissionService submissionService;

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
     * Create a new review
     */
    public TaskReview createReview(UserTask userTask, com.example.studenttask.model.User reviewer,
                                 String comment, Integer rating, Long submissionId) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewedAt(LocalDateTime.now());

        // If submissionId is provided, find and set the submission
        if (submissionId != null) {
            // Find submission by userTask and submissionId
            List<Submission> submissions = submissionService.getSubmissionsByUserTask(userTask);
            Optional<Submission> targetSubmission = submissions.stream()
                    .filter(s -> s.getId().equals(submissionId))
                    .findFirst();
            review.setSubmission(targetSubmission.orElse(null));
        } else {
            // If no specific submission is selected, use the latest submission
            Optional<Submission> latestSubmission = submissionService.getLatestSubmission(userTask);
            review.setSubmission(latestSubmission.orElse(null));
        }

        return save(review);
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