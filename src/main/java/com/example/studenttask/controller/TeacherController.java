package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskViewService taskViewService;

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());
        List<Task> recentTasks = taskService.findByCreatedBy(teacher);

        model.addAttribute("teacher", teacher);
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("totalTasks", recentTasks.size());

        return "teacher/dashboard";
    }

    

    @GetMapping("/tasks/create")
    public String createTaskForm(Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());
        List<TaskView> taskViews = taskViewService.findAll();

        model.addAttribute("teacher", teacher);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("task", new Task());

        return "teacher/task-create";
    }

    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task, 
                           @RequestParam("taskViewId") String taskViewId,
                           @RequestParam("groupIds") List<Long> groupIds,
                           Principal principal) {

        User teacher = userService.findByPreferredUsername(principal.getName());
        Optional<TaskView> taskViewOpt = taskViewService.findById(taskViewId);

        if (taskViewOpt.isEmpty()) {
            // Handle case where TaskView is not found
            return "redirect:/teacher/tasks/create?error=invalid_view";
        }

        TaskView taskView = taskViewOpt.get();
        task.setCreatedBy(teacher);
        task.setTaskView(taskView);

        Task savedTask = taskService.createTask(task, groupIds);

        return "redirect:/teacher/tasks/" + savedTask.getId();
    }

    @GetMapping("/tasks/{taskId}")
    public String taskDetail(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());
        Optional<Task> taskOpt = taskService.findById(taskId);

        if (taskOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();

        // Sicherheitscheck: Nur Ersteller kann Task anzeigen
        if (!task.getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);

        return "teacher/task-detail";
    }

    
}