package com.example.studenttask.service;

import com.example.studenttask.dto.TaskContentCommandResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskApiCommandService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiCommandService.class);

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    public TaskContentCommandResultDto saveTeacherTaskContent(Long userTaskId, String content, String authenticationName) {
        log.debug("Saving teacher content for userTask {} by user {} with length {}",
            userTaskId,
            authenticationName,
            content != null ? content.length() : null);

        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            log.warn("UserTask {} not found while saving teacher content", userTaskId);
            return TaskContentCommandResultDto.notFound();
        }

        UserTask userTask = userTaskOpt.get();
        log.debug("Found UserTask {} (user={}, task={})",
            userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

        TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
        log.debug("Saved content {} with version {} for UserTask {}",
            savedContent.getId(), savedContent.getVersion(), userTask.getId());

        return TaskContentCommandResultDto.success(savedContent);
    }

    public TaskContentCommandResultDto saveTaskContent(Long taskId, String openIdSubject, String content) {
        log.debug("Saving task content for task {} and user {}", taskId, openIdSubject);
        log.debug("Content length: {}", content != null ? content.length() : null);
        if (content != null && !content.isEmpty()) {
            log.debug("Content preview: {}", preview(content, 100));
        }

        Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
        if (userOpt.isEmpty()) {
            log.warn("User not found while saving content: {}", openIdSubject);
            return TaskContentCommandResultDto.unauthorized();
        }
        User user = userOpt.get();
        log.debug("Found user {} for save request", user.getId());

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Task {} not found while saving content", taskId);
            return TaskContentCommandResultDto.notFound();
        }
        Task task = taskOpt.get();
        log.debug("Found task {} ({})", task.getId(), task.getTitle());

        UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
        log.debug("Using UserTask {} (user={}, task={})",
            userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

        TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
        log.debug("Saved content {} version {} for task {}",
            savedContent.getId(), savedContent.getVersion(), taskId);
        log.debug("Saved content length: {}",
            savedContent.getContent() != null ? savedContent.getContent().length() : null);
        log.debug("Saved at: {}", savedContent.getSavedAt());
        log.debug("Submitted flag: {}", savedContent.isSubmitted());

        return TaskContentCommandResultDto.success(savedContent);
    }

    public TaskContentCommandResultDto submitTask(Long taskId, String openIdSubject, String content) {
        log.debug("Submitting task {} for user {}", taskId, openIdSubject);

        Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
        if (userOpt.isEmpty()) {
            log.warn("User {} not found while submitting task {}", openIdSubject, taskId);
            return TaskContentCommandResultDto.unauthorized();
        }

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Task {} not found while submitting", taskId);
            return TaskContentCommandResultDto.notFound();
        }

        UserTask userTask = userTaskService.findOrCreateUserTask(userOpt.get(), taskOpt.get());

        if (content != null) {
            log.debug("Submitting provided content for UserTask {} with length {}",
                userTask.getId(), content.length());
            TaskContent submittedContent = taskContentService.submitContent(userTask, content);
            return TaskContentCommandResultDto.success(submittedContent);
        }

        Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
        if (latestContentOpt.isPresent()) {
            log.debug("Submitting latest saved content for UserTask {}", userTask.getId());
            TaskContent submittedContent = taskContentService.submitContent(userTask, latestContentOpt.get().getContent());
            return TaskContentCommandResultDto.success(submittedContent);
        }

        log.debug("No content available to submit for UserTask {}", userTask.getId());
        return TaskContentCommandResultDto.success();
    }

    private String preview(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}
