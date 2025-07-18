
package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskContentController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    /**
     * Get current content for a task
     */
    @GetMapping("/{taskId}/content")
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Principal principal) {
        try {
            User user = userService.findByPreferredUsername(principal.getName());
            Optional<Task> taskOpt = taskService.findById(taskId);

            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task task = taskOpt.get();
            
            // Get or create UserTask
            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            
            // Get latest content
            Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
            
            if (latestContent.isPresent()) {
                return ResponseEntity.ok(latestContent.get().getContent());
            } else {
                // Return default submission if no content exists yet
                String defaultContent = task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
                return ResponseEntity.ok(defaultContent);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Save content for a task
     */
    @PostMapping("/{taskId}/content")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId, 
                                                 @RequestBody String content, 
                                                 Principal principal) {
        try {
            User user = userService.findByPreferredUsername(principal.getName());
            Optional<Task> taskOpt = taskService.findById(taskId);

            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task task = taskOpt.get();
            
            // Get or create UserTask
            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            
            // Save content as draft
            taskContentService.saveContent(userTask, content, false);
            
            return ResponseEntity.ok("Content saved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Submit a task
     */
    @PostMapping("/{taskId}/submit")
    public ResponseEntity<String> submitTask(@PathVariable Long taskId, Principal principal) {
        try {
            User user = userService.findByPreferredUsername(principal.getName());
            Optional<Task> taskOpt = taskService.findById(taskId);

            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task task = taskOpt.get();
            
            // Get UserTask
            UserTask userTask = userTaskService.findOrCreateUserTask(user, task);
            
            // Get latest content
            Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
            
            if (latestContent.isPresent()) {
                // Mark as submitted
                taskContentService.submitContent(userTask, latestContent.get().getContent());
                return ResponseEntity.ok("Task submitted successfully");
            } else {
                return ResponseEntity.badRequest().body("No content to submit");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
