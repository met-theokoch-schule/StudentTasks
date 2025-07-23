package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
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
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
    public String dashboard(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Hauptstatistik: Noch zu bewertende Aufgaben
        int pendingReviews = countPendingReviewsForTeacher(teacher);
        model.addAttribute("pendingReviews", pendingReviews);

        // Letzte 5 erstellte Aufgaben
        List<Task> tasks = taskService.findByCreatedBy(teacher);
        List<Task> recentTasks = tasks.stream()
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentTasks", recentTasks);

        model.addAttribute("teacher", teacher);
        return "teacher/dashboard";
    }

    /**
     * Zählt alle noch zu bewertenden Aufgaben für einen Lehrer.
     * Berücksichtigt nur Abgaben mit Status ABGEGEBEN, bei denen sowohl
     * Schüler als auch Lehrer die entsprechende Gruppe der Aufgabe teilen.
     */
    private int countPendingReviewsForTeacher(User teacher) {
        // Alle Gruppen des Lehrers
        Set<Group> teacherGroups = teacher.getGroups();

        // Alle aktiven Aufgaben des Lehrers
        List<Task> teacherTasks = taskService.findByCreatedBy(teacher);

        int pendingCount = 0;

        for (Task task : teacherTasks) {
            // Für jede Aufgabe: Prüfe welche Gruppen zugeordnet sind
            Set<Group> taskGroups = task.getAssignedGroups();

            // Nur Gruppen betrachten, die sowohl der Aufgabe als auch dem Lehrer zugeordnet sind
            Set<Group> relevantGroups = taskGroups.stream()
                    .filter(teacherGroups::contains)
                    .collect(Collectors.toSet());

            if (!relevantGroups.isEmpty()) {
                // Alle UserTasks für diese Aufgabe mit Status ABGEGEBEN
                List<UserTask> submittedUserTasks = userTaskService.findByTaskAndStatusName(task, "ABGEGEBEN");

                // Nur Schüler zählen, die in einer der relevanten Gruppen sind
                for (UserTask userTask : submittedUserTasks) {
                    User student = userTask.getUser();
                    Set<Group> studentGroups = student.getGroups();

                    // Prüfen ob Schüler in mindestens einer relevanten Gruppe ist
                    boolean hasSharedGroup = studentGroups.stream()
                            .anyMatch(relevantGroups::contains);

                    if (hasSharedGroup) {
                        pendingCount++;
                    }
                }
            }
        }

        return pendingCount;
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

    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task,
                           @RequestParam(value = "selectedGroups", required = false) List<Long> selectedGroupIds,
                           @RequestParam("taskViewId") Long taskViewId,
                           @RequestParam(required = false) String unitTitleId,
                           RedirectAttributes redirectAttributes,
                           Principal principal) {

        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        TaskView taskView = taskViewService.findById(taskViewId)
            .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));

        task.setTaskView(taskView);
        task.setCreatedBy(teacher);
        task.setCreatedAt(LocalDateTime.now());
        task.setIsActive(true);

        // Gruppen zuweisen
        if (selectedGroupIds != null && !selectedGroupIds.isEmpty()) {
            List<Group> selectedGroups = groupService.findAllById(selectedGroupIds);
            task.setAssignedGroups(new HashSet<>(selectedGroups));
        } else {
            task.setAssignedGroups(new HashSet<>());
        }


        taskService.save(task);

        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde erfolgreich erstellt.");
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
```