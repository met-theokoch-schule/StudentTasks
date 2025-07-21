
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable Long taskId, Authentication authentication, Model model) {
        User currentUser = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/student/dashboard";
        }
        
        Task task = taskOpt.get();

        // Get or create UserTask
        UserTask userTask = userTaskService.findOrCreateUserTask(currentUser, task);

        // Get current content
        Optional<TaskContent> currentContentOpt = taskContentService.getLatestContent(userTask);
        String content = currentContentOpt.map(TaskContent::getContent)
                .orElse(task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("content", content);
        model.addAttribute("renderedDescription", task.getDescription());

        // Return the appropriate task view template
        return "taskviews/" + task.getTaskView().getId();
    }

    @GetMapping("/tasks/{taskId}/iframe")
    public String viewTaskIframe(@PathVariable Long taskId, 
                                @RequestParam(required = false) Long userId,
                                @RequestParam(required = false) Integer version,
                                Authentication authentication, Model model) {
        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/student/dashboard";
        }
        
        Task task = taskOpt.get();

        User targetUser;
        if (userId != null) {
            // For teacher viewing student work
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return "redirect:/student/dashboard";
            }
            targetUser = userOpt.get();
        } else {
            // For student viewing own work
            targetUser = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (targetUser == null) {
                return "redirect:/login";
            }
        }

        // Get or create UserTask
        UserTask userTask = userTaskService.findOrCreateUserTask(targetUser, task);

        // Get content for specific version or latest
        TaskContent content;
        if (version != null) {
            Optional<TaskContent> contentOpt = taskContentService.getContentByVersion(userTask, version);
            content = contentOpt.orElse(null);
        } else {
            Optional<TaskContent> contentOpt = taskContentService.getLatestContent(userTask);
            content = contentOpt.orElse(null);
        }

        String contentText = content != null ? content.getContent() : 
                (task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("content", contentText);
        model.addAttribute("renderedDescription", task.getDescription());
        model.addAttribute("isIframe", true);
        model.addAttribute("isTeacherView", userId != null);

        // Return the appropriate task view template
        return "taskviews/" + task.getTaskView().getId();
    }

    @PostMapping("/api/tasks/{taskId}/submit")
    @ResponseBody
    public ResponseEntity<String> submitTask(@PathVariable Long taskId, 
                                           @RequestBody Map<String, String> request,
                                           Authentication authentication) {
        try {
            String content = request.get("content");
            User user = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Optional<Task> taskOpt = taskService.findById(taskId);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            boolean success = submissionService.submitTask(user, taskOpt.get(), content);
            if (success) {
                return ResponseEntity.ok("Task submitted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to submit task");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/api/usertasks/{userTaskId}/content")
    @ResponseBody
    public ResponseEntity<String> saveContentForUserTask(@PathVariable Long userTaskId,
                                                        @RequestBody Map<String, String> request,
                                                        Authentication authentication) {
        try {
            String content = request.get("content");

            // Verify teacher permissions
            User teacher = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (teacher == null || !teacher.hasRole("TEACHER")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
            if (userTaskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();

            // Save content with teacher as modifier (but keep original user)
            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);

            return ResponseEntity.ok("Content saved successfully. Version: " + savedContent.getVersion());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
