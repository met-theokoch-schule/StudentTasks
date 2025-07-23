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
        User teacher = userService.findByOpenIdSubject(principal.getName()).orElse(null);
        if (teacher == null) {
            return "redirect:/login";
        }

        // Berechne zu bewertende Aufgaben (Status: ABGEGEBEN)
        // Nur Aufgaben zählen, bei denen Lehrer und Schüler die gleiche Gruppe haben
        int pendingReviews = calculatePendingReviews(teacher);
        model.addAttribute("pendingReviews", pendingReviews);

        // Neueste Aufgaben für Dashboard
        List<Task> recentTasks = taskService.findByCreatedByOrderByCreatedAtDesc(teacher)
            .stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentTasks", recentTasks);

        return "teacher/dashboard";
    }

    @GetMapping("/reviews/pending")
    public String pendingReviews(Model model, Authentication authentication) {
        User teacher = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
        if (teacher == null) {
            return "redirect:/home";
        }

        // Alle UserTasks mit Status ABGEGEBEN finden, die zu den Aufgaben des Lehrers gehören
        List<Task> teacherTasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);
        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews = new LinkedHashMap<>();

        for (Task task : teacherTasks) {
            List<UserTask> userTasks = userTaskService.findByTask(task);
            List<UserTask> pendingUserTasks = new ArrayList<>();

            for (UserTask userTask : userTasks) {
                // Prüfe ob Lehrer und Schüler eine gemeinsame Gruppe haben
                User student = userTask.getUser();
                Set<Group> teacherGroups = new HashSet<>(teacher.getGroups());
                Set<Group> studentGroups = new HashSet<>(student.getGroups());

                // Prüfe, ob Aufgabe einer Gruppe zugeordnet ist, die sowohl Lehrer als auch Schüler haben
                Set<Group> assignedGroups = task.getAssignedGroups();
                for (Group assignedGroup : assignedGroups) {
                    if (teacherGroups.contains(assignedGroup) && studentGroups.contains(assignedGroup)) {
                        // Prüfe Status ABGEGEBEN
                        if (userTask.getStatus() != null && "ABGEGEBEN".equals(userTask.getStatus().getName())) {
                            pendingUserTasks.add(userTask);
                            break; // Pro UserTask nur einmal zählen
                        }
                    }
                }
            }

            if (!pendingUserTasks.isEmpty()) {
                UnitTitle unitTitle = task.getUnitTitle();
                groupedPendingReviews.computeIfAbsent(unitTitle, k -> new LinkedHashMap<>())
                    .put(task, pendingUserTasks);
            }
        }

        model.addAttribute("groupedPendingReviews", groupedPendingReviews);
        return "teacher/pending-reviews";
    }

    private int calculatePendingReviews(User teacher) {
        // Alle Gruppen des Lehrers
        Set<Group> teacherGroups = teacher.getGroups();

        // Alle aktiven Aufgaben des Lehrers
        List<Task> activeTasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);

        int count = 0;

        for (Task task : activeTasks) {
            // Alle UserTasks für diese Aufgabe mit Status ABGEGEBEN
            List<UserTask> submittedUserTasks = userTaskService.findByTask(task)
                .stream()
                .filter(userTask -> {
                    TaskStatus status = userTask.getStatus();
                    return status != null && "ABGEGEBEN".equals(status.getName());
                })
                .collect(Collectors.toList());

            for (UserTask userTask : submittedUserTasks) {
                User student = userTask.getUser();
                Set<Group> studentGroups = student.getGroups();

                // Prüfe, ob Aufgabe einer Gruppe zugeordnet ist, die sowohl Lehrer als auch Schüler haben
                Set<Group> assignedGroups = task.getAssignedGroups();
                for (Group assignedGroup : assignedGroups) {
                    if (teacherGroups.contains(assignedGroup) && studentGroups.contains(assignedGroup)) {
                        count++;
                        break; // Pro UserTask nur einmal zählen
                    }
                }
            }
        }

        return count;
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