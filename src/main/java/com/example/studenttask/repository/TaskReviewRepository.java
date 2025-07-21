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
     * Alle Reviews für eine UserTask finden, sortiert nach Review-Datum
     */
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);

    /**
     * Das neueste Review für eine UserTask finden
     */
    Optional<TaskReview> findFirstByUserTaskOrderByReviewedAtDesc(UserTask userTask);

    /**
     * Alle Reviews eines Reviewers finden
     */
    List<TaskReview> findByReviewerOrderByReviewedAtDesc(User reviewer);
}
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {
    
    /**
     * Find all reviews for a user task ordered by review date descending
     */
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);
    
    /**
     * Find all reviews by a specific reviewer ordered by review date descending
     */
    List<TaskReview> findByReviewerOrderByReviewedAtDesc(User reviewer);
}
