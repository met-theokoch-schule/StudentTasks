
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {
    
    /**
     * Find all reviews for a specific user task
     */
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);
    
    /**
     * Find all reviews by a specific reviewer
     */
    List<TaskReview> findByReviewerOrderByReviewedAtDesc(User reviewer);
    
    /**
     * Find the latest review for a user task
     */
    @Query("SELECT tr FROM TaskReview tr WHERE tr.userTask = :userTask ORDER BY tr.reviewedAt DESC LIMIT 1")
    TaskReview findLatestByUserTask(@Param("userTask") UserTask userTask);
}
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {
    List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask);
    List<TaskReview> findByReviewerOrderByReviewedAtDesc(com.example.studenttask.model.User reviewer);
}
