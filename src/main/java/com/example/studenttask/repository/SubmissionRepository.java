
package com.example.studenttask.repository;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByUserTaskOrderBySubmittedAtDesc(UserTask userTask);
    
    List<Submission> findByUserTask(UserTask userTask);
}
