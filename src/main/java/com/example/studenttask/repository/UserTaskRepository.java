package com.example.studenttask.repository;

import com.example.studenttask.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    /**
     * Find UserTask by user and task
     */
    Optional<UserTask> findByUserAndTask(User user, Task task);

    /**
     * Alle UserTasks für einen User
     */
    List<UserTask> findByUser(User user);

    /**
     * Find all UserTasks for a specific task
     */
    List<UserTask> findByTask(Task task);

    List<UserTask> findByTaskAndStatus(Task task, TaskStatus status);

    @Query("SELECT ut FROM UserTask ut WHERE ut.user = :user AND ut.lastModified IS NOT NULL ORDER BY ut.lastModified DESC")
    List<UserTask> findByUserOrderByLastModifiedDesc(@Param("user") User user, Pageable pageable);

    /**
     * Alle UserTasks für einen User
     */
    List<UserTask> findByUserAndStatus(User user, TaskStatus status);

    /**
     * UserTasks nach Task und Status
     */
    List<UserTask> findByTaskAndStatus(Task task, TaskStatus status);

    /**
     * Anzahl UserTasks nach Status
     */
    long countByStatus(TaskStatus status);

    List<UserTask> findByUserOrderByStartedAtDesc(User user);

    Optional<UserTask> findByUserIdAndTaskId(Long userId, Long taskId);

    List<UserTask> findByUserAndTaskIn(User user, List<Task> tasks);

    @Query("SELECT COUNT(ut) FROM UserTask ut " +
           "WHERE ut.task IN :tasks " +
           "AND ut.status.name = 'ABGEGEBEN'")
    long countPendingReviewTasksForTasks(@Param("tasks") List<Task> tasks);
}