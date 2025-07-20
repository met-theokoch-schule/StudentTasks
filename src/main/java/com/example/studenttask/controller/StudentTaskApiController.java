
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

            // Get latest content
            TaskContent latestContent = taskContentService.findLatestByUserTask(userTaskOpt.get());
            if (latestContent == null || latestContent.getContent() == null) {
                // Return default submission if no content exists
                String defaultContent = userTaskOpt.get().getTask().getDefaultSubmission();
                return ResponseEntity.ok(defaultContent != null ? defaultContent : "");
            }

            return ResponseEntity.ok(latestContent.getContent());
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

            // Save content as draft
            taskContentService.saveContent(userTaskOpt.get(), content, false);
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

            // Get latest content and mark as submitted
            TaskContent latestContent = taskContentService.findLatestByUserTask(userTask);
            if (latestContent != null) {
                taskContentService.submitContent(latestContent);
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
