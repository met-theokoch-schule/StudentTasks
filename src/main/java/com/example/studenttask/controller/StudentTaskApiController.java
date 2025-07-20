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
import com.example.studenttask.model.User;
import com.example.studenttask.model.Task;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import com.example.studenttask.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class StudentTaskApiController {

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TaskService taskService;

    @GetMapping("/{taskId}/content")
    public ResponseEntity<Map<String, Object>> getTaskContent(@PathVariable Long taskId, 
                                                             Authentication authentication) {
        try {
            System.out.println("🔍 === DEBUG: Get Content API Called ===");
            System.out.println("   - Task ID: " + taskId);
            System.out.println("   - User: " + authentication.getName());

            // Find user
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                System.out.println("   - ERROR: User not found: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("   - User found: " + user.getName() + " (ID: " + user.getId() + ")");

            // Find UserTask
            Task task = taskService.findById(taskId).orElse(null);
            if (task == null) {
                System.out.println("   - ERROR: Task not found: " + taskId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            System.out.println("   - Task found: " + task.getTitle() + " (ID: " + task.getId() + ")");

            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            System.out.println("   - UserTask: " + userTask.getId());

            // Get latest content
            TaskContent latestContent = taskContentService.getLatestContent(userTask);

            Map<String, Object> response = new HashMap<>();
            if (latestContent != null) {
                response.put("content", latestContent.getContent());
                response.put("version", latestContent.getVersion());
                System.out.println("   - Found content: Version " + latestContent.getVersion() + ", Length: " + 
                                 (latestContent.getContent() != null ? latestContent.getContent().length() : "null"));
            } else {
                response.put("content", "");
                response.put("version", 0);
                System.out.println("   - No content found, returning empty");
            }
            System.out.println("🔍 === DEBUG: Get Content API End ===");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("   - ERROR in get content: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{taskId}/save")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId, 
                                                 @RequestBody Map<String, String> payload,
                                                 Authentication authentication) {
        try {
            String content = payload.get("content");
            System.out.println("🔍 === DEBUG: Save Content API Called ===");
            System.out.println("   - Task ID: " + taskId);
            System.out.println("   - Content length: " + (content != null ? content.length() : "null"));
            System.out.println("   - User: " + authentication.getName());

            // Find user
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                System.out.println("   - ERROR: User not found: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            System.out.println("   - User found: " + user.getName() + " (ID: " + user.getId() + ")");

            // Find or create UserTask
            Task task = taskService.findById(taskId).orElse(null);
            if (task == null) {
                System.out.println("   - ERROR: Task not found: " + taskId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
            System.out.println("   - Task found: " + task.getTitle() + " (ID: " + task.getId() + ")");

            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            System.out.println("   - UserTask: " + userTask.getId());

            // Save content
            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
            System.out.println("   - Content saved with ID: " + savedContent.getId() + ", Version: " + savedContent.getVersion());
            System.out.println("🔍 === DEBUG: Save Content API End ===");

            return ResponseEntity.ok("Content saved successfully");
        } catch (Exception e) {
            System.out.println("   - ERROR in save: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving content: " + e.getMessage());
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