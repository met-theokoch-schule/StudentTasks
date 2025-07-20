package com.example.studenttask.repository;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {

    /**
     * Alle Reviews für eine UserTask finden, sortiert nach Erstellungsdatum
     */
    List<TaskReview> findByUserTaskOrderByCreatedAtDesc(UserTask userTask);
    
    /**
     * Alle Reviews für eine UserTask finden, sortiert nach Review-Datum
     */
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);

    /**
     * Das neueste Review für eine UserTask finden
     */
    Optional<TaskReview> findFirstByUserTaskOrderByCreatedAtDesc(UserTask userTask);

    /**
     * Alle Reviews eines Reviewers finden
     */
    List<TaskReview> findByReviewerIdOrderByCreatedAtDesc(String reviewerId);
}