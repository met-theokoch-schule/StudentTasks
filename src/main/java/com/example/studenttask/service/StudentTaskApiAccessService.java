package com.example.studenttask.service;

import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskApiAccessService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiAccessService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    public Optional<UserTask> findUserTask(Long taskId, String openIdSubject) {
        User user = requireUser(openIdSubject);
        Task task = requireTask(taskId);
        return userTaskService.findByUserIdAndTaskId(user.getId(), task.getId());
    }

    public UserTask findOrCreateUserTask(Long taskId, String openIdSubject) {
        User user = requireUser(openIdSubject);
        Task task = requireTask(taskId);
        return userTaskService.findOrCreateUserTask(user, task);
    }

    public UserTask requireUserTask(Long userTaskId) {
        return userTaskService.findById(userTaskId)
            .orElseThrow(() -> {
                log.warn("UserTask {} not found while resolving API access", userTaskId);
                return new ApiNotFoundException("UserTask nicht gefunden");
            });
    }

    private User requireUser(String openIdSubject) {
        return userService.findByOpenIdSubject(openIdSubject)
            .orElseThrow(() -> {
                log.warn("User not found while resolving API access: {}", openIdSubject);
                return new ApiUnauthorizedException("Benutzer nicht gefunden");
            });
    }

    private Task requireTask(Long taskId) {
        return taskService.findById(taskId)
            .orElseThrow(() -> {
                log.warn("Task {} not found while resolving API access", taskId);
                return new ApiNotFoundException("Aufgabe nicht gefunden");
            });
    }
}
