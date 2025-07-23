package com.example.studenttask.service;

import com.example.studenttask.model.*;
import com.example.studenttask.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserTaskService {

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Find UserTasks by task
     */
    public List<UserTask> findByTask(Task task) {
        return userTaskRepository.findByTask(task);
    }

    /**
     * Find or create UserTask for user and task
     */
    public UserTask findOrCreateUserTask(User user, Task task) {
        Optional<UserTask> existingUserTask = userTaskRepository.findByUserAndTask(user, task);
        if (existingUserTask.isPresent()) {
            return existingUserTask.get();
        } else {
            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTask(task);
            userTask.setStartedAt(LocalDateTime.now());
            userTask.setLastModified(LocalDateTime.now());

            // Set initial status (assuming NICHT_BEGONNEN exists)
            // This should be improved to get the actual initial status
            return userTaskRepository.save(userTask);
        }
    }

    /**
     * Status einer UserTask ändern (mit Validierung)
     */
    public boolean updateStatus(UserTask userTask, TaskStatus newStatus) {
        if (!taskStatusService.canTransitionTo(userTask.getStatus(), newStatus)) {
            return false; // Übergang nicht erlaubt
        }

        userTask.setStatus(newStatus);
        userTask.setLastModified(LocalDateTime.now());

        // Wenn zum ersten Mal begonnen wird
        if ("IN_BEARBEITUNG".equals(newStatus.getName()) && userTask.getStartedAt() == null) {
            userTask.setStartedAt(LocalDateTime.now());
        }

        userTaskRepository.save(userTask);
        return true;
    }

    /**
     * Alle UserTasks für einen User
     */
    public List<UserTask> findByUser(User user) {
        return userTaskRepository.findByUser(user);
    }

    /**
     * UserTasks nach Status filtern
     */
    public List<UserTask> findByUserAndStatus(User user, TaskStatus status) {
        return userTaskRepository.findByUserAndStatus(user, status);
    }

    /**
     * Mögliche nächste Status für eine UserTask
     */
    public List<TaskStatus> getNextPossibleStatuses(UserTask userTask) {
        return taskStatusService.getNextPossibleStatuses(userTask.getStatus());
    }

    /**
     * UserTask speichern
     */
    public UserTask save(UserTask userTask) {
        userTask.setLastModified(LocalDateTime.now());
        return userTaskRepository.save(userTask);
    }

    /**
     * UserTask löschen
     */
    public void delete(UserTask userTask) {
        userTaskRepository.delete(userTask);
    }

    public UserTask findById(Long id) {
        return userTaskRepository.findById(id).orElse(null);
    }

    public Optional<UserTask> findByUserAndTask(User user, Task task) {
        return userTaskRepository.findByUserAndTask(user, task);
    }

    public Optional<UserTask> findByUserIdAndTaskId(Long userId, Long taskId) {
        return userTaskRepository.findByUserIdAndTaskId(userId, taskId);
    }

    public void updateStatus(UserTask userTask, String statusName) {
        Optional<TaskStatus> status = taskStatusService.findByName(statusName);
        if (status.isPresent()) {
            userTask.setStatus(status.get());
            userTaskRepository.save(userTask);
        }
    }
}