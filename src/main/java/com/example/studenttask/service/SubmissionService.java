
package com.example.studenttask.service;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
