package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserTaskService userTaskService;

    /**
     * Lehrer-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        List<Task> recentTasks = taskService.findByCreatedBy(teacher);

        model.addAttribute("teacher", teacher);
        model.addAttribute("recentTasks", recentTasks);

        return "teacher/dashboard";
    }



    /**
     * Zeigt das Formular zum Erstellen einer neuen Aufgabe
     */
    @GetMapping("/tasks/create")
    public String createTaskForm(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = new Task();
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();

        // Get all groups from GroupService to ensure proper loading
        List<Group> allGroups = groupService.findAll();

        // Filter groups that belong to this teacher
        List<Group> teacherGroups = allGroups.stream()
            .filter(group -> teacher.getGroups().contains(group))
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews != null ? taskViews : new ArrayList<>());
        model.addAttribute("groups", teacherGroups);
        model.addAttribute("teacher", teacher);

        return "teacher/task-create";
    }

    /**
     * Verarbeitet das Erstellen einer neuen Aufgabe
     */
    @PostMapping("/tasks")
    public String createTask(
            @ModelAttribute Task task,
            @RequestParam(value = "selectedGroups", required = false) List<String> selectedGroups,
            @RequestParam("taskViewId") Long taskViewId,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            // Task View aus dem Service laden
            TaskView taskView = taskViewService.findById(taskViewId.toString())
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);

            // Ersteller setzen
            task.setCreatedBy(teacher);
            task.setCreatedAt(LocalDateTime.now());

            // Aufgabe speichern
            Task savedTask = taskService.save(task);

            // Gruppen zuordnen
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                Set<Group> assignedGroups = new HashSet<>();
                for (String groupId : selectedGroups) {
                    Group group = groupService.findById(Long.parseLong(groupId));
                    if (group != null) {
                        assignedGroups.add(group);
                    }
                }
                savedTask.setAssignedGroups(assignedGroups);
                taskService.save(savedTask);
            }

            redirectAttributes.addFlashAttribute("success", "Aufgabe '" + savedTask.getTitle() + "' wurde erfolgreich erstellt.");
            return "redirect:/teacher/tasks";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Erstellen der Aufgabe: " + e.getMessage());
            return "redirect:/teacher/tasks/create";
        }
    }

    /**
     * Zeigt Details einer Aufgabe mit allen Abgaben
     */
    @GetMapping("/tasks/{taskId}")
    public String taskDetail(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

        // Sicherheitscheck: Nur eigene Aufgaben anzeigen
        if (!task.getCreatedBy().equals(teacher)) {
            throw new RuntimeException("Zugriff verweigert");
        }

        List<UserTask> submissions = userTaskService.findByTask(task);

        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);
        model.addAttribute("teacher", teacher);

        return "teacher/task-submissions";
    }

    /**
     * Löscht eine Aufgabe
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        try {
            Task task = taskService.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

            // Sicherheitscheck: Nur eigene Aufgaben löschen
            if (!task.getCreatedBy().equals(teacher)) {
                throw new RuntimeException("Zugriff verweigert");
            }

            taskService.deleteTask(taskId);
            redirectAttributes.addFlashAttribute("success", "Aufgabe wurde erfolgreich gelöscht.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Löschen der Aufgabe: " + e.getMessage());
        }

        return "redirect:/teacher/tasks";
    }

    /**
     * Speichert eine Aufgabe als Entwurf
     */
    @PostMapping("/tasks/draft")
    public ResponseEntity<String> saveDraft(@ModelAttribute Task task, 
                                          @RequestParam("selectedGroups") List<Long> selectedGroupIds,
                                          @RequestParam("taskViewId") Long taskViewId,
                                          Principal principal) {
        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            // Task View aus dem Service laden
            TaskView taskView = taskViewService.findById(taskViewId.toString())
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);

            // Lehrer zuweisen
            task.setCreatedBy(teacher);

            // Als Entwurf markieren (inaktiv)
            task.setActive(false);

            // Gruppen zuweisen
            if (selectedGroupIds != null && !selectedGroupIds.isEmpty()) {
                Set<Group> selectedGroups = new HashSet<>();
                for (Long groupId : selectedGroupIds) {
                    Group group = groupService.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("Gruppe nicht gefunden: " + groupId));
                    selectedGroups.add(group);
                }
                task.setAssignedGroups(selectedGroups);
            }

            // Aufgabe speichern
            Task savedTask = taskService.save(task);
            return ResponseEntity.ok("Entwurf erfolgreich gespeichert");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Speichern: " + e.getMessage());
        }
    }
}