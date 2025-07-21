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

    // This method was causing a conflict with StudentController#viewTask
    // The StudentController already handles /student/tasks/{taskId}
    // If we need teacher review functionality, we should use a different path like:
    // @GetMapping("/teacher/tasks/{taskId}/review")
    // public String reviewTask(@PathVariable Long taskId, @RequestParam(required = false) Long userId, Authentication authentication, Model model) {
    //     // Teacher review implementation would go here
    //     return "teacher/task-review";
    // }

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
            // Teacher reviewing student's work - use openIdSubject lookup since we don't have findById
            // For now, skip user lookup for teacher review mode
            targetUser = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (targetUser == null) {
                return "redirect:/teacher/dashboard";
            }
        } else {
            // Student accessing their own task
            targetUser = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (targetUser == null) {
                return "redirect:/login";
            }
        }

        // Get or create UserTask
        UserTask userTask = userTaskService.findOrCreateUserTask(targetUser, task);

        // Get task content based on version
        TaskContent content = null;
        if (version != null) {
            TaskContent foundContent = taskContentService.getContentByVersion(userTask, version);
            content = foundContent;
        } else {
            // Get latest content or use task's default submission
            Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
            if (latestContentOpt.isEmpty()) {
                // Create a temporary TaskContent with default submission from task
                content = new TaskContent(userTask, task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "", 0);
            } else {
                content = latestContentOpt.get();
            }
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
}