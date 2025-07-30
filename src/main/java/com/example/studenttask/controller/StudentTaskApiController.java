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
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Authentication authentication) {
        try {
            System.out.println("🔍 === DEBUG: Get Content API Called ===");
            System.out.println("   - Task ID: " + taskId);
            System.out.println("   - User: " + authentication.getName());

            // Get current user
            String openIdSubject = authentication.getName();
            Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
            if (userOpt.isEmpty()) {
                System.out.println("   - User not found: " + openIdSubject);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
            User user = userOpt.get();
            System.out.println("   - Found user: " + user.getId() + " (" + user.getName() + ")");

            // Get task
            Optional<Task> taskOpt = taskService.findById(taskId);
            if (taskOpt.isEmpty()) {
                System.out.println("   - Task not found!");
                return ResponseEntity.notFound().build();
            }
            Task task = taskOpt.get();
            System.out.println("   - Found task: " + task.getId() + " (" + task.getTitle() + ")");

            // Find UserTask
            Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.getId(), task.getId());
            if (userTaskOpt.isEmpty()) {
                System.out.println("   - UserTask not found!");
                return ResponseEntity.ok(""); // Return empty content for new tasks
            }
            UserTask userTask = userTaskOpt.get();
            System.out.println("   - Found UserTask: " + userTask.getId());

            // Get latest content from TaskContentService
            Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
            String content = "";
            if (latestContent.isPresent()) {
                content = latestContent.get().getContent();
                System.out.println("   - Found latest content: version " + latestContent.get().getVersion());
                System.out.println("   - Content length: " + (content != null ? content.length() : "null"));
                System.out.println("   - Content preview: " + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content));
            } else {
                System.out.println("   - No content found, returning empty string");
            }

            System.out.println("🔍 === DEBUG: Get Content API END ===");

            return ResponseEntity.ok(content != null ? content : "");
        } catch (Exception e) {
            System.err.println("Error loading task content: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/usertasks/{userTaskId}/content")
    public ResponseEntity<String> saveUserTaskContent(@PathVariable Long userTaskId,
                                                     @RequestBody Map<String, String> request,
                                                     Authentication authentication) {
        try {
            System.out.println("🔍 === DEBUG: Save UserTask Content API Called ===");
            System.out.println("   - UserTask ID: " + userTaskId);
            System.out.println("   - User: " + authentication.getName());
            
            String content = request.get("content");
            System.out.println("   - Content length: " + (content != null ? content.length() : "null"));

            // Find the UserTask directly
            Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
            if (userTaskOpt.isEmpty()) {
                System.out.println("   - UserTask not found with ID: " + userTaskId);
                return ResponseEntity.notFound().build();
            }
            
            UserTask userTask = userTaskOpt.get();

            System.out.println("   - Found UserTask: " + userTask.getId() + " (User: " + userTask.getUser().getId() + ", Task: " + userTask.getTask().getId() + ")");

            // Save content
            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
            System.out.println("   - Content saved with ID: " + savedContent.getId() + ", Version: " + savedContent.getVersion());

            return ResponseEntity.ok("Content saved successfully");
        } catch (Exception e) {
            System.out.println("   - Error saving content: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving content");
        }
    }

    @PostMapping("/{taskId}/content")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId, 
                                                 @RequestBody Map<String, String> request,
                                                 Authentication authentication) {
        try {
            System.out.println("🔍 === DEBUG: Save Content API Called ===");
            System.out.println("   - Task ID: " + taskId);
            System.out.println("   - User: " + authentication.getName());
            System.out.println("   - Authentication type: " + authentication.getClass().getSimpleName());
            System.out.println("   - Request body keys: " + request.keySet());
            System.out.println("   - Request body: " + request);

            String content = request.get("content");
            System.out.println("   - Content is null: " + (content == null));
            System.out.println("   - Content length: " + (content != null ? content.length() : "null"));
            if (content != null && content.length() > 0) {
                System.out.println("   - Content preview: " + content.substring(0, Math.min(100, content.length())) + (content.length() > 100 ? "..." : ""));
            }

            // Find user
            String openIdSubject = authentication.getName();
            Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
            if (userOpt.isEmpty()) {
                System.out.println("   - ERROR: User not found: " + openIdSubject);
                System.out.println("   - Available users in database:");
                // Add some debug info about available users
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + openIdSubject);
            }
            User user = userOpt.get();
            System.out.println("   - User found: " + user.getName() + " (ID: " + user.getId() + ", Username: " + user.getName() + ")");

            // Find task
            System.out.println("   - Looking for task with ID: " + taskId);
            Task task = taskService.findById(taskId).orElse(null);
            if (task == null) {
                System.out.println("   - ERROR: Task not found: " + taskId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found: " + taskId);
            }
            System.out.println("   - Task found: " + task.getTitle() + " (ID: " + task.getId() + ")");

            // Find or create UserTask
            System.out.println("   - Finding or creating UserTask for user " + user.getId() + " and task " + task.getId());
            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            System.out.println("   - UserTask: " + userTask.getId() + " (User: " + userTask.getUser().getId() + ", Task: " + userTask.getTask().getId() + ")");

            // Save content
            System.out.println("   - Saving content with TaskContentService...");
            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
            System.out.println("   - Content saved with ID: " + savedContent.getId() + ", Version: " + savedContent.getVersion());
            System.out.println("   - Saved content length: " + (savedContent.getContent() != null ? savedContent.getContent().length() : "null"));
            System.out.println("   - Saved at: " + savedContent.getSavedAt());
            System.out.println("   - Is submitted: " + savedContent.isSubmitted());
            System.out.println("🔍 === DEBUG: Save Content API End ===");

            return ResponseEntity.ok("Content saved successfully (ID: " + savedContent.getId() + ", Version: " + savedContent.getVersion() + ")");
        } catch (Exception e) {
            System.out.println("   - ERROR in save: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("   - Stack trace:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving content: " + e.getClass().getSimpleName() + ": " + e.getMessage());
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