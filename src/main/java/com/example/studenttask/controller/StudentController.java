
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private TaskViewService taskViewService;

    /**
     * Schüler-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle UserTasks des Schülers
        List<UserTask> userTasks = userTaskService.findByUser(student);

        // Nur aktive Aufgaben anzeigen
        List<UserTask> activeTasks = userTasks.stream()
            .filter(ut -> ut.getTask().getIsActive())
            .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("userTasks", activeTasks);

        return "student/dashboard";
    }

    /**
     * Aufgabe bearbeiten
     */
    @GetMapping("/tasks/{taskId}")
    public String editTask(@PathVariable Long taskId, Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

        // UserTask finden oder erstellen
        UserTask userTask = userTaskService.findOrCreateUserTask(student, task);

        // Neuesten Content laden
        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);

        // TaskView laden
        TaskView taskView = taskViewService.findById(task.getTaskView().getId())
            .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("taskView", taskView);
        model.addAttribute("student", student);
        
        if (latestContent.isPresent()) {
            model.addAttribute("currentContent", latestContent.get().getContent());
        } else {
            model.addAttribute("currentContent", task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");
        }

        return "student/task-edit";
    }

    /**
     * Aufgabenliste für Schüler
     */
    @GetMapping("/tasks")
    public String taskList(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle UserTasks des Schülers
        List<UserTask> userTasks = userTaskService.findByUser(student);

        // Nur aktive Aufgaben anzeigen
        List<UserTask> activeTasks = userTasks.stream()
            .filter(ut -> ut.getTask().getIsActive())
            .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("userTasks", activeTasks);

        return "student/tasks-list";
    }
}
