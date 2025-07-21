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
}