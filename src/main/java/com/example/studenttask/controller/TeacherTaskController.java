package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    /**
     * Zeigt die Übersicht aller Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());
        List<Task> tasks = taskService.findByCreatedBy(teacher);

        model.addAttribute("teacher", teacher);
        model.addAttribute("tasks", tasks);

        return "teacher/tasks-list";
    }

    /**
     * Zeigt die Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());
        Task task = taskService.findById(taskId);

        // Sicherheit: Prüfen ob Aufgabe existiert und dem Lehrer gehört
        if (task == null || !task.getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        List<UserTask> submissions = userTaskService.findByTask(task);

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);

        return "teacher/task-submissions";
    }
}