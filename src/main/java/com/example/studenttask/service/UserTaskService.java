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
     * Alle UserTasks für eine bestimmte Task finden
     */
    public List<UserTask> findByTask(Task task) {
        return userTaskRepository.findByTask(task);
    }

    /**
     * UserTask für User und Task finden oder erstellen
     */
    public UserTask findOrCreateUserTask(User user, Task task) {
        UserTask existing = userTaskRepository.findByUserAndTask(user, task);

        if (existing != null) {
            return existing;
        }

        // Neue UserTask erstellen mit Default-Status
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setStatus(taskStatusService.getDefaultStatus());
        userTask.setStartedAt(LocalDateTime.now());
        userTask.setLastModified(LocalDateTime.now());

        return userTaskRepository.save(userTask);
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
}