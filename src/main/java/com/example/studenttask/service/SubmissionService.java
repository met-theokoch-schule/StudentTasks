
package com.example.studenttask.service;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    /**
     * Create a new submission
     */
    public Submission createSubmission(UserTask userTask, TaskContent taskContent) {
        Submission submission = new Submission(userTask, taskContent);
        return submissionRepository.save(submission);
    }

    /**
     * Get all submissions for a user task
     */
    public List<Submission> getSubmissionsByUserTask(UserTask userTask) {
        return submissionRepository.findByUserTaskOrderBySubmittedAtDesc(userTask);
    }

    /**
     * Get latest submission for a user task
     */
    public Optional<Submission> getLatestSubmission(UserTask userTask) {
        List<Submission> submissions = submissionRepository.findByUserTaskOrderBySubmittedAtDesc(userTask);
        return submissions.isEmpty() ? Optional.empty() : Optional.of(submissions.get(0));
    }

    /**
     * Check if user task has any submissions
     */
    public boolean hasSubmissions(UserTask userTask) {
        return !submissionRepository.findByUserTask(userTask).isEmpty();
    }

    /**
     * Get submission count for user task
     */
    public int getSubmissionCount(UserTask userTask) {
        return submissionRepository.findByUserTask(userTask).size();
    }

    /**
     * Delete all submissions for a user task
     */
    public void deleteAllSubmissionsForUserTask(UserTask userTask) {
        List<Submission> submissions = submissionRepository.findByUserTask(userTask);
        submissionRepository.deleteAll(submissions);
    }
}
