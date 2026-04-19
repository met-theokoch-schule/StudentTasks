package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private TaskViewService taskViewService;

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

        TaskView taskView = resolveTaskView(task);
        if (taskView == null || taskView.getTemplatePath() == null || taskView.getTemplatePath().isBlank()) {
            return userId != null ? "redirect:/teacher/dashboard" : "redirect:/student/dashboard";
        }

        model.addAttribute("task", task);
        model.addAttribute("taskView", taskView);
        model.addAttribute("userTask", userTask);
        model.addAttribute("userTaskId", userTask.getId());
        model.addAttribute("currentContent", contentText);
        model.addAttribute("renderedDescription", task.getDescription());
        model.addAttribute("isIframe", true);
        model.addAttribute("isTeacherView", userId != null);

        return taskView.getTemplatePath();
    }

    private TaskView resolveTaskView(Task task) {
        TaskView taskView = task.getTaskView();
        if (taskView == null) {
            return null;
        }

        Long taskViewId = taskView.getId();
        if (taskViewId == null) {
            return taskView;
        }

        return taskViewService.findById(taskViewId).orElse(taskView);
    }
}
