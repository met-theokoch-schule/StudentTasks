package com.example.studenttask.controller;

import com.example.studenttask.model.Group;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        // Neue Task für Formular erstellen
        Task task = new Task();
        task.setActive(true); // Standard: aktiv

        // Verfügbare TaskViews laden
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();

        // Verfügbare Gruppen laden (alle Gruppen des Lehrers)
        List<Group> groups = teacher.getGroups().stream().collect(Collectors.toList());

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("groups", groups);

        return "teacher/task-create";
    }

    @PostMapping("/tasks")
    public String createTask(@ModelAttribute("task") Task task, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User teacher = userService.findByPreferredUsername(principal.getName());

            // Aufgabe konfigurieren
            task.setCreatedBy(teacher);
            task.setCreatedAt(LocalDateTime.now());

            // TaskView setzen
            if (task.getTaskView() != null && task.getTaskView().getId() != null) {
                Optional<TaskView> taskViewOpt = taskViewService.findById(task.getTaskView().getId());
                if (taskViewOpt.isPresent()) {
                    task.setTaskView(taskViewOpt.get());
                } else {
                    redirectAttributes.addFlashAttribute("error", "Ungültiger Aufgabentyp ausgewählt.");
                    return "redirect:/teacher/tasks/create";
                }
            }

            // Aufgabe speichern
            Task savedTask = taskService.createTask(task, new ArrayList<>());

            redirectAttributes.addFlashAttribute("success", "Aufgabe '" + savedTask.getTitle() + "' wurde erfolgreich erstellt.");
            return "redirect:/teacher/tasks";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Erstellen der Aufgabe: " + e.getMessage());
            return "redirect:/teacher/tasks/create";
        }
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