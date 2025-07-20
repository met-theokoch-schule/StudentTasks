package com.example.studenttask.controller;

import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.service.UserTaskService;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class StudentTaskApiController {

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @GetMapping("/{taskId}/content")
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Authentication authentication) {
        try {
            // Get current user
            var user = userService.findByOpenIdSubject(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            // Find UserTask for this user and task
            Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.get().getId(), taskId);
            if (userTaskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();

            // Get current content or create empty
            Optional<TaskContent> currentContentOpt = taskContentService.getLatestContent(userTask);
            String currentContent = currentContentOpt.map(TaskContent::getContent).orElse("");

            return ResponseEntity.ok(currentContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{taskId}/content")
    public ResponseEntity<Void> saveTaskContent(@PathVariable Long taskId, 
                                               @RequestBody String content, 
                                               Authentication authentication) {
        try {
            // Get current user
            var user = userService.findByOpenIdSubject(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            // Find UserTask for this user and task
            Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.get().getId(), taskId);
            if (userTaskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();
            // Save content as draft
            taskContentService.saveContent(userTask, content, false);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<Void> submitTask(@PathVariable Long taskId, Authentication authentication) {
        try {
            // Get current user
            var user = userService.findByOpenIdSubject(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            // Find UserTask for this user and task
            Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.get().getId(), taskId);
            if (userTaskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();

            // Get latest content to submit
            Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
            if (latestContentOpt.isPresent()) {
                // Create submitted version from latest content
                taskContentService.submitContent(userTask, latestContentOpt.get().getContent());
            }

            // Update UserTask status to "ABGEGEBEN"
            userTaskService.updateStatus(userTask, "ABGEGEBEN");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}