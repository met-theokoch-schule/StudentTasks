package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class StudentTaskApiController {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiController.class);

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/{taskId}/content")
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Authentication authentication) {
        try {
            log.debug("Loading task content for task {} and user {}", taskId, authentication.getName());

            String openIdSubject = authentication.getName();
            Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
            if (userOpt.isEmpty()) {
                log.warn("User not found while loading task content: {}", openIdSubject);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
            User user = userOpt.get();
            log.debug("Found user {} for content request", user.getId());

            Optional<Task> taskOpt = taskService.findById(taskId);
            if (taskOpt.isEmpty()) {
                log.warn("Task {} not found while loading content", taskId);
                return ResponseEntity.notFound().build();
            }
            Task task = taskOpt.get();
            log.debug("Found task {} ({})", task.getId(), task.getTitle());

            Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.getId(), task.getId());
            if (userTaskOpt.isEmpty()) {
                log.debug("No UserTask found for user {} and task {}", user.getId(), task.getId());
                return ResponseEntity.ok("");
            }
            UserTask userTask = userTaskOpt.get();
            log.debug("Found UserTask {}", userTask.getId());

            Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
            String content = "";
            if (latestContent.isPresent()) {
                content = latestContent.get().getContent();
                log.debug("Found latest content for UserTask {} with version {} and length {}",
                        userTask.getId(),
                        latestContent.get().getVersion(),
                        content != null ? content.length() : null);
                log.debug("Content preview: {}", preview(content, 50));
            } else {
                log.debug("No content found for UserTask {}", userTask.getId());
            }

            return ResponseEntity.ok(content != null ? content : "");
        } catch (Exception e) {
            log.error("Error loading task content for task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
    @PostMapping("/usertasks/{userTaskId}/content")
    public ResponseEntity<String> saveUserTaskContent(@PathVariable Long userTaskId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String content = request.get("content");
            log.debug("Saving teacher content for userTask {} by user {} with length {}",
                    userTaskId,
                    authentication.getName(),
                    content != null ? content.length() : null);

            Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
            if (userTaskOpt.isEmpty()) {
                log.warn("UserTask {} not found while saving teacher content", userTaskId);
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();
            log.debug("Found UserTask {} (user={}, task={})",
                    userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
            log.debug("Saved content {} with version {} for UserTask {}",
                    savedContent.getId(), savedContent.getVersion(), userTask.getId());

            return ResponseEntity.ok("Content saved successfully");
        } catch (Exception e) {
            log.error("Error saving teacher content for userTask {}", userTaskId, e);
            return ResponseEntity.status(500).body("Error saving content");
        }
    }

    @PostMapping("/{taskId}/content")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            log.debug("Saving task content for task {} and user {}", taskId, authentication.getName());
            log.debug("Request keys: {}", request.keySet());

            String content = request.get("content");
            log.debug("Content length: {}", content != null ? content.length() : null);
            if (content != null && !content.isEmpty()) {
                log.debug("Content preview: {}", preview(content, 100));
            }

            String openIdSubject = authentication.getName();
            Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
            if (userOpt.isEmpty()) {
                log.warn("User not found while saving content: {}", openIdSubject);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + openIdSubject);
            }
            User user = userOpt.get();
            log.debug("Found user {} for save request", user.getId());

            Task task = taskService.findById(taskId).orElse(null);
            if (task == null) {
                log.warn("Task {} not found while saving content", taskId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found: " + taskId);
            }
            log.debug("Found task {} ({})", task.getId(), task.getTitle());

            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            log.debug("Using UserTask {} (user={}, task={})",
                    userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
            log.debug("Saved content {} version {} for task {}",
                    savedContent.getId(), savedContent.getVersion(), taskId);
            log.debug("Saved content length: {}", savedContent.getContent() != null ? savedContent.getContent().length() : null);
            log.debug("Saved at: {}", savedContent.getSavedAt());
            log.debug("Submitted flag: {}", savedContent.isSubmitted());

            return ResponseEntity.ok("Content saved successfully (ID: " + savedContent.getId() + ", Version: "
                    + savedContent.getVersion() + ")");
        } catch (Exception e) {
            log.error("Error saving task content for task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving content: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<Void> submitTask(@PathVariable Long taskId,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {
        try {
            log.debug("Submitting task {} for user {}", taskId, authentication.getName());

            var user = userService.findByOpenIdSubject(authentication.getName());
            if (user.isEmpty()) {
                log.warn("User {} not found while submitting task {}", authentication.getName(), taskId);
                return ResponseEntity.status(401).build();
            }

            Task task = taskService.findById(taskId).orElse(null);
            if (task == null) {
                log.warn("Task {} not found while submitting", taskId);
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskService.findOrCreateUserTask(user.get(), task);
            String content = request != null ? request.get("content") : null;

            if (content != null) {
                log.debug("Submitting provided content for UserTask {} with length {}",
                        userTask.getId(), content.length());
                taskContentService.submitContent(userTask, content);
            } else {
                Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
                if (latestContentOpt.isPresent()) {
                    log.debug("Submitting latest saved content for UserTask {}", userTask.getId());
                    taskContentService.submitContent(userTask, latestContentOpt.get().getContent());
                } else {
                    log.debug("No content available to submit for UserTask {}", userTask.getId());
                }
            }

            log.debug("Submitted UserTask {}", userTask.getId());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error submitting task {}", taskId, e);
            return ResponseEntity.status(500).build();
        }
    }

    private String preview(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}
