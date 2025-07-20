
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
        Optional<TaskStatus> statusOpt = taskStatusRepository.findById(statusId);
        if (statusOpt.isPresent()) {
            review.setStatus(statusOpt.get());
            
            // Update UserTask status
            userTask.setStatus(statusOpt.get());
            userTask.setLastModified(LocalDateTime.now());
            userTaskService.save(userTask);
        }
        
        // Set submission if provided
        if (submissionId != null) {
            Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
            submissionOpt.ifPresent(review::setSubmission);
        }
        
        return taskReviewRepository.save(review);
    }

    public List<TaskReview> findAll() {
        return taskReviewRepository.findAll();
    }

    public Optional<TaskReview> findById(Long id) {
        return taskReviewRepository.findById(id);
    }
}
