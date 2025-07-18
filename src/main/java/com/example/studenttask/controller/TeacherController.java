package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TaskViewService taskViewService;

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
     * Zeigt alle Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        List<Task> tasks = taskService.findByCreatedBy(teacher);

        model.addAttribute("tasks", tasks);
        model.addAttribute("teacher", teacher);

        return "teacher/tasks-list";
    }

    /**
     * Zeigt das Formular zum Erstellen einer neuen Aufgabe
     */
    @GetMapping("/tasks/create")
    public String showCreateTaskForm(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = new Task();
        List<TaskView> taskViews = taskViewService.findAll();
        Set<Group> teacherGroups = teacher.getGroups();

        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("teacherGroups", teacherGroups);
        model.addAttribute("teacher", teacher);

        return "teacher/task-create";
    }

    /**
     * Verarbeitet das Erstellen einer neuen Aufgabe
     */
    @PostMapping("/tasks/create")
    public String createTask(
            @ModelAttribute Task task,
            @RequestParam(value = "selectedGroups", required = false) List<String> selectedGroups,
            @RequestParam("taskViewId") Long taskViewId,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            // Task View setzen
            TaskView taskView = taskViewService.findById(taskViewId)
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);

            // Ersteller setzen
            task.setCreatedBy(teacher);
            task.setCreatedAt(LocalDateTime.now());

            // Standard-Submission setzen falls leer
            if (task.getDefaultSubmission() == null || task.getDefaultSubmission().trim().isEmpty()) {
                task.setDefaultSubmission("");
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
                assignedGroups = teacher.getGroups().stream()
                    .filter(group -> groupIds.contains(group.getId()))
                    .collect(Collectors.toSet());
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
     * Zeigt eine Liste aller Gruppen mit aktiven Aufgaben
     */
    @GetMapping("/groups")
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Set<Group> teacherGroups = teacher.getGroups();

        model.addAttribute("groups", teacherGroups);
        model.addAttribute("teacher", teacher);

        return "teacher/groups-list";
    }

    /**
     * Zeigt Details einer Gruppe mit allen Schülern und deren Aufgaben
     */
    @GetMapping("/groups/{groupId}")
    public String groupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Gruppe finden
        Group group = teacher.getGroups().stream()
            .filter(g -> g.getId().equals(groupId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Gruppe nicht gefunden oder Zugriff verweigert"));

        // Alle Schüler der Gruppe laden
        Set<User> students = group.getUsers();

        // Alle Aufgaben des Lehrers für diese Gruppe laden
        List<Task> tasks = taskService.findByCreatedBy(teacher).stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        model.addAttribute("group", group);
        model.addAttribute("students", students);
        model.addAttribute("tasks", tasks);
        model.addAttribute("teacher", teacher);

        return "teacher/group-detail";
    }
}