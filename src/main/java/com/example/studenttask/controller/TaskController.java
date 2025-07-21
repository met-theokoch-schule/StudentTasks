
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable Long taskId, Authentication authentication, Model model) {
        User currentUser = userService.findByOpenIdSubject(authentication.getName());
        
        Task task = taskService.findById(taskId);
        if (task == null) {
            return "redirect:/student/dashboard";
        }

        // Get or create UserTask
        UserTask userTask = userTaskService.findByUserAndTask(currentUser, task);
        if (userTask == null) {
            userTask = userTaskService.createUserTask(currentUser, task);
        }

        // Get current content
        TaskContent currentContent = taskContentService.getLatestContent(userTask);
        String content = currentContent != null ? currentContent.getContent() : task.getDefaultSubmission();

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
        Task task = taskService.findById(taskId);
        if (task == null) {
            return "redirect:/student/dashboard";
        }

        User targetUser;
        if (userId != null) {
            // For teacher viewing student work
            targetUser = userService.findById(userId);
        } else {
            // For student viewing own work
            targetUser = userService.findByOpenIdSubject(authentication.getName());
        }

        // Get or create UserTask
        UserTask userTask = userTaskService.findByUserAndTask(targetUser, task);
        if (userTask == null) {
            userTask = userTaskService.createUserTask(targetUser, task);
        }

        // Get content for specific version or latest
        TaskContent content;
        if (version != null) {
            content = taskContentService.getContentByVersion(userTask, version);
        } else {
            content = taskContentService.getLatestContent(userTask);
        }

        String contentText = content != null ? content.getContent() : task.getDefaultSubmission();

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
