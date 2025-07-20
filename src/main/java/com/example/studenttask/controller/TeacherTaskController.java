package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskViewService taskViewService;

    /**
     * Zeigt die Übersicht aller Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        System.out.println("🔍 === DEBUG: Task List Loading ===");
        System.out.println("   - Loading tasks for teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");
        
        List<Task> tasks = taskService.findByCreatedBy(teacher);
        System.out.println("   - Found " + tasks.size() + " tasks in task list");
        for (Task task : tasks) {
            System.out.println("   - Task: " + task.getTitle() + " (ID: " + task.getId() + ", Active: " + task.getIsActive() + ")");
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("tasks", tasks);

        return "teacher/tasks-list";
    }

    /**
     * Zeigt die Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        Optional<Task> taskOpt = taskService.findById(taskId);

        // Sicherheit: Prüfen ob Aufgabe existiert und dem Lehrer gehört
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();

        List<UserTask> submissions = userTaskService.findByTask(task);

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);

        return "teacher/task-submissions";
    }
}