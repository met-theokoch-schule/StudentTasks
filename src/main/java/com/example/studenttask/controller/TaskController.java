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
            // Teacher reviewing student's work
            Optional<User> userOpt = userService.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return "redirect:/teacher/dashboard";
            }
            targetUser = userOpt.get();
        } else {
            // Student accessing their own task
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
}