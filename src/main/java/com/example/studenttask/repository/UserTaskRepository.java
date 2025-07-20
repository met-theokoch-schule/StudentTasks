package com.example.studenttask.repository;

import com.example.studenttask.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
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
}