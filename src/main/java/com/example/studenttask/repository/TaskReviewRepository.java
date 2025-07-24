package com.example.studenttask.repository;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {

    /**
     * Find all reviews for a user task ordered by review date descending
     */
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);
    
    boolean existsByUserTaskAndVersion(UserTask userTask, Integer version);
    
    long countByUserTaskAndVersion(UserTask userTask, Integer version);

    /**
     * Find all reviews by a specific reviewer ordered by review date descending
     */
    List<TaskReview> findByReviewerOrderByReviewedAtDesc(User reviewer);

    /**
     * Find the latest review for a user task
     */
    Optional<TaskReview> findFirstByUserTaskOrderByReviewedAtDesc(UserTask userTask);
}