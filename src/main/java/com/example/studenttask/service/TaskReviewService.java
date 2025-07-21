package com.example.studenttask.service;

import com.example.studenttask.model.*;
import com.example.studenttask.repository.TaskReviewRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class TaskReviewService {

    @Autowired
    private TaskReviewRepository taskReviewRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskStatusService taskStatusService;

    public List<TaskReview> findByUserTask(UserTask userTask) {
        return taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask);
    }

    public TaskReview createReview(UserTask userTask, User reviewer, Long statusId, String comment, Long submissionId) {
        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());

        // Set status
        TaskStatus status = taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        review.setStatus(status);

        // Set submission - if not provided, find the latest submission
        Submission submission;
        if (submissionId != null) {
            submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));
        } else {
            // Find latest submission for this user task
            List<Submission> submissions = submissionRepository.findByUserTaskOrderBySubmittedAtDesc(userTask);
            if (submissions.isEmpty()) {
                throw new RuntimeException("No submissions found for this task - cannot create review");
            }
            submission = submissions.get(0);
        }
        review.setSubmission(submission);

        // Update UserTask status
        userTask.setStatus(status);
        userTask.setLastModified(LocalDateTime.now());

        return taskReviewRepository.save(review);
    }

    public List<TaskReview> findAll() {
        return taskReviewRepository.findAll();
    }

    public Optional<TaskReview> findById(Long id) {
        return taskReviewRepository.findById(id);
    }

    /**
     * Verfügbare Status für Lehrer-Reviews (nur VOLLSTÄNDIG und ÜBERARBEITUNG_NÖTIG)
     */
    public List<TaskStatus> getAvailableReviewStatuses() {
        List<TaskStatus> teacherStatuses = new ArrayList<>();

        // Nur diese beiden Status sind für Lehrer-Reviews sinnvoll
        taskStatusService.findByName("VOLLSTÄNDIG").ifPresent(teacherStatuses::add);
        taskStatusService.findByName("ÜBERARBEITUNG_NÖTIG").ifPresent(teacherStatuses::add);

        return teacherStatuses;
    }
}
package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskReviewService {

    @Autowired
    private TaskReviewRepository taskReviewRepository;

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
}
