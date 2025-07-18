package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Lehrer nicht gefunden"));

        // Neue Task für Formular erstellen
        Task task = new Task();
        task.setActive(true); // Standard: aktiv

        // Verfügbare TaskViews laden
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();

        // Verfügbare Gruppen laden (alle Gruppen des Lehrers)
        List<Group> groups = teacher.getGroups() != null ? 
            teacher.getGroups().stream().collect(Collectors.toList()) : 
            new ArrayList<>();

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("groups", groups);

        return "teacher/task-create";
    }

    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task, 
                            @RequestParam(required = false) Long taskViewId,
                            @RequestParam(required = false) List<String> selectedGroups,
                            BindingResult bindingResult, Model model, Principal principal,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "teacher/task-create";
        }

        try {
            // Lehrer laden
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Lehrer nicht gefunden"));
            task.setCreatedBy(teacher);
            task.setCreatedAt(LocalDateTime.now());

            // TaskView basierend auf ID setzen
            if (taskViewId != null) {
                Optional<TaskView> taskViewOpt = taskViewService.findById(taskViewId);
                if (taskViewOpt.isPresent()) {
                    task.setTaskView(taskViewOpt.get());
                }
            }

            // Basisaufgabe: Wenn keine View ausgewählt, setze "Basisaufgabe"
            if (task.getTaskView() == null) {
                Optional<TaskView> baseTaskViewOpt = taskViewService.findByName("Basisaufgabe");
                if (baseTaskViewOpt.isPresent()) {
                    TaskView baseTaskView = baseTaskViewOpt.get();
                    task.setTaskView(baseTaskView);
                } else {
                    // Fallback: Erste verfügbare TaskView verwenden
                    List<TaskView> taskViews = taskViewService.findActiveTaskViews();
                    if (!taskViews.isEmpty()) {
                        task.setTaskView(taskViews.get(0));
                    }
                }
            }

            // selectedGroups von String zu Long konvertieren
            List<Long> groupIds = new ArrayList<>();
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                for (String groupIdStr : selectedGroups) {
                    try {
                        groupIds.add(Long.parseLong(groupIdStr));
                    } catch (NumberFormatException e) {
                        // Ignoriere ungültige IDs
                    }
                }
            }

            // Ausgewählte Gruppen zuweisen
            Set<Group> assignedGroups = new HashSet<>();
            if (!groupIds.isEmpty()) {
                for (Long groupId : groupIds) {
                    teacher.getGroups().stream()
                        .filter(group -> group.getId().equals(groupId))
                        .findFirst()
                        .ifPresent(assignedGroups::add);
                }
            }
            task.setAssignedGroups(assignedGroups);

            // Aufgabe speichern
            Task savedTask = taskService.createTask(task.getTitle(), task.getDescription(), 
                task.getDefaultSubmission(), teacher, task.getDueDate(), task.getTaskView(), 
                assignedGroups);

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