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

        // Tasks des Lehrers laden
        System.out.println("🔍 === DEBUG: Dashboard Task Loading ===");
        System.out.println("   - Loading tasks for teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");
        List<Task> tasks = taskService.findByCreatedBy(teacher);
        System.out.println("   - Found " + tasks.size() + " tasks");
        for (Task task : tasks) {
            System.out.println("   - Task: " + task.getTitle() + " (ID: " + task.getId() + ", CreatedBy: " + 
                (task.getCreatedBy() != null ? task.getCreatedBy().getName() + " (ID: " + task.getCreatedBy().getId() + ")" : "NULL") + 
                ", Active: " + task.getIsActive() + ")");
        }

        // Task-Statistiken berechnen
        TaskService.TaskStatistics stats = taskService.getTaskStatistics(teacher);

        model.addAttribute("teacher", teacher);
        model.addAttribute("recentTasks", tasks);

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
            System.out.println("🔍 === DEBUG: Task Creation START ===");
            System.out.println("   - Task title: " + task.getTitle());
            System.out.println("   - Task description: " + task.getDescription());
            System.out.println("   - Selected groups: " + selectedGroups);
            System.out.println("   - TaskView ID: " + taskViewId);

            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
            System.out.println("   - Teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");

            // Task View aus dem Service laden
            TaskView taskView = taskViewService.findById(taskViewId)
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);
            System.out.println("   - TaskView: " + taskView.getName() + " (ID: " + taskView.getId() + ")");

            // Ersteller setzen
            task.setCreatedBy(teacher);
            System.out.println("   - CreatedBy set to teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");
            task.setCreatedAt(LocalDateTime.now());
            System.out.println("   - CreatedAt set to: " + task.getCreatedAt());

            // Task aktiv setzen
            task.setIsActive(true);
            System.out.println("   - Task set to active: " + task.getIsActive());

            // Gruppen verarbeiten
            Set<Group> assignedGroups = new HashSet<>();
            System.out.println("   - Processing groups...");
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                System.out.println("   - Groups to process: " + selectedGroups.size());
                for (String groupIdStr : selectedGroups) {
                    try {
                        Long groupId = Long.parseLong(groupIdStr);
                        Group group = groupService.findById(groupId);
                        if (group != null) {
                            assignedGroups.add(group);
                            System.out.println("   - Added group: " + group.getName() + " (ID: " + groupId + ")");
                        } else {
                            System.out.println("   - Group not found: " + groupId);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("   - Invalid group ID: " + groupIdStr);
                    }
                }
            } else {
                System.out.println("   - No groups selected");
            }
            task.setAssignedGroups(assignedGroups);
            System.out.println("   - Total assigned groups: " + assignedGroups.size());

            // Task vor dem Speichern prüfen
            System.out.println("   - Task before saving:");
            System.out.println("     - Title: " + task.getTitle());
            System.out.println("     - CreatedBy: " + (task.getCreatedBy() != null ? task.getCreatedBy().getName() + " (ID: " + task.getCreatedBy().getId() + ")" : "NULL"));
            System.out.println("     - IsActive: " + task.getIsActive());
            System.out.println("     - TaskView: " + (task.getTaskView() != null ? task.getTaskView().getName() : "NULL"));

            // Task speichern
            Task savedTask = taskService.save(task);
            System.out.println("   - Task saved with ID: " + savedTask.getId());
            System.out.println("   - Saved task CreatedBy: " + (savedTask.getCreatedBy() != null ? savedTask.getCreatedBy().getName() + " (ID: " + savedTask.getCreatedBy().getId() + ")" : "NULL"));
            System.out.println("🔍 === DEBUG: Task Creation END ===");

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
     * Zeigt das Formular zum Bearbeiten einer Aufgabe
     */
    @GetMapping("/tasks/{taskId}/edit")
    public String editTaskForm(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();
        List<Group> allGroups = groupService.findAll();

        // Set current taskViewId for form
        Long currentTaskViewId = task.getTaskView() != null ? task.getTaskView().getId() : null;

        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("groups", allGroups);
        model.addAttribute("selectedGroups", task.getAssignedGroups().stream().map(Group::getId).collect(Collectors.toList()));
        model.addAttribute("currentTaskViewId", currentTaskViewId);

        return "teacher/task-edit";
    }

    @PostMapping("/tasks/{taskId}/edit")
    public String editTask(@PathVariable Long taskId,
                         @ModelAttribute Task task,
                         @RequestParam("taskViewId") Long taskViewId,
                         @RequestParam(value = "selectedGroups", required = false) List<Long> selectedGroups,
                         RedirectAttributes redirectAttributes,
                         Principal principal) {
        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            Task existingTask = taskService.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

            // Sicherheitscheck: Nur eigene Aufgaben bearbeiten
            if (!existingTask.getCreatedBy().equals(teacher)) {
                throw new RuntimeException("Zugriff verweigert");
            }

            // TaskView laden
            TaskView taskView = taskViewService.findById(taskViewId)
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);
            existingTask.setTaskView(taskView);

            // Gruppen aktualisieren
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                Set<Group> assignedGroups = new HashSet<>();
                for (Long groupId : selectedGroups) {
                    Group group = groupService.findById(groupId);
                    if (group != null) {
                        assignedGroups.add(group);
                    }
                }
                existingTask.setAssignedGroups(assignedGroups);
            } else {
                existingTask.setAssignedGroups(new HashSet<>());
            }

            // Daten aktualisieren
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setIsActive(task.getIsActive());

            taskService.save(existingTask);

            redirectAttributes.addFlashAttribute("success", "Aufgabe '" + existingTask.getTitle() + "' wurde erfolgreich aktualisiert.");
            return "redirect:/teacher/tasks";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Aktualisieren der Aufgabe: " + e.getMessage());
            return "redirect:/teacher/tasks/edit/" + taskId;
        }
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
            TaskView taskView = taskViewService.findById(taskViewId)
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
                    Group group = groupService.findById(groupId);
                    if (group == null) {
                        throw new RuntimeException("Gruppe nicht gefunden: " + groupId);
                    }
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