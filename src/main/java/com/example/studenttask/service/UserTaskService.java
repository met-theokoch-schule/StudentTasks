package com.example.studenttask.service;

import com.example.studenttask.exception.TaskInvariantViolationException;
import com.example.studenttask.model.*;
import com.example.studenttask.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
            userTask.setStatus(taskStatusService.getDefaultStatus());
            return userTaskRepository.save(userTask);
        }
    }

    /**
     * Status einer UserTask ändern (mit Validierung)
     */
    public boolean updateStatus(UserTask userTask, TaskStatus newStatus) {
        TaskStatus currentStatus = userTask.getStatus();
        if (currentStatus == null) {
            currentStatus = taskStatusService.getDefaultStatus();
            userTask.setStatus(currentStatus);
        }

        if (isSameLogicalStatus(currentStatus, newStatus)) {
            userTask.setLastModified(LocalDateTime.now());
            userTaskRepository.save(userTask);
            return true;
        }

        if (!taskStatusService.canTransitionTo(currentStatus, newStatus)) {
            return false; // Übergang nicht erlaubt
        }

        userTask.setStatus(newStatus);
        userTask.setLastModified(LocalDateTime.now());

        // Wenn zum ersten Mal begonnen wird
        if (taskStatusService.isStatus(newStatus, TaskStatusCode.IN_BEARBEITUNG) && userTask.getStartedAt() == null) {
            userTask.setStartedAt(LocalDateTime.now());
        }

        userTaskRepository.save(userTask);
        return true;
    }

    public boolean updateStatus(UserTask userTask, TaskStatusCode statusCode) {
        return updateStatus(userTask, taskStatusService.requireStatus(statusCode));
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
        assertNoDuplicateAssignment(userTask);
        userTask.setLastModified(LocalDateTime.now());
        return userTaskRepository.save(userTask);
    }

    /**
     * UserTask löschen
     */
    public void delete(UserTask userTask) {
        userTaskRepository.delete(userTask);
    }

    public Optional<UserTask> findById(Long id) {
        return userTaskRepository.findById(id);
    }

    public Optional<UserTask> findByUserIdAndTaskId(Long userId, Long taskId) {
        return userTaskRepository.findByUserIdAndTaskId(userId, taskId);
    }

    private boolean isSameLogicalStatus(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        Optional<TaskStatusCode> currentCode = TaskStatusSupport.getCode(currentStatus);
        Optional<TaskStatusCode> newCode = TaskStatusSupport.getCode(newStatus);
        if (currentCode.isPresent() && newCode.isPresent()) {
            return currentCode.get() == newCode.get();
        }

        if (currentStatus.getId() != null && currentStatus.getId().equals(newStatus.getId())) {
            return true;
        }

        return Objects.equals(currentStatus.getName(), newStatus.getName());
    }

    private void assertNoDuplicateAssignment(UserTask userTask) {
        if (userTask == null || userTask.getUser() == null || userTask.getTask() == null) {
            return;
        }

        Optional<UserTask> existingUserTask =
            userTaskRepository.findByUserAndTask(userTask.getUser(), userTask.getTask());
        if (existingUserTask.isPresent() && !Objects.equals(existingUserTask.get().getId(), userTask.getId())) {
            throw new TaskInvariantViolationException("UserTask exists already for this user and task");
        }
    }

}
