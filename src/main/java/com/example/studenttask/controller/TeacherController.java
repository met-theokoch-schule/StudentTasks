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
     * Zeigt das Formular zum Löschen einer Aufgabe
     */


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

    @Autowired
    private com.example.studenttask.service.TaskContentService taskContentService;

    @GetMapping("/teacher/submissions/{userTaskId}/view")
    public String viewSubmissionContent(@PathVariable Long userTaskId, 
                                       @RequestParam(required = false) Integer version,
                                       Authentication authentication, Model model) {
        // Get the user task
        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return "redirect:/teacher/dashboard";
        }

        UserTask userTask = userTaskOpt.get();
        Task task = userTask.getTask();

        String contentText;

        if (version != null && version > 0) {
            // Load specific version
            TaskContent versionContent = taskContentService.getContentByVersion(userTask, version);
            if (versionContent != null) {
                contentText = versionContent.getContent();
            } else {
                // Fallback if version not found
                Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
                contentText = latestContentOpt.map(TaskContent::getContent)
                    .orElse(task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");
            }
        } else {
            // Show latest content by default
            Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
            contentText = latestContentOpt.map(TaskContent::getContent)
                .orElse(task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");
        }

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("content", contentText);
        model.addAttribute("renderedDescription", task.getDescription());
        model.addAttribute("isIframe", true);
        model.addAttribute("isTeacherView", true);

        // Return the appropriate task view template
        return "taskviews/" + task.getTaskView().getId();
    }
}