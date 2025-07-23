package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.example.studenttask.repository.*;
import com.example.studenttask.service.UserService;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;

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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskReviewService taskReviewService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TaskContentRepository taskContentRepository;

    /**
     * Direkte Aufgaben-Ansicht (ohne task-edit.html Wrapper)
     */
    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable Long taskId, Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Aufgabe nicht gefunden");
        }
        Task task = taskOpt.get();

        // Check if student has access to this task
        Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(student.getId(), task.getId());
        if (userTaskOpt.isEmpty()) {
            throw new RuntimeException("Keine Berechtigung für diese Aufgabe");
        }
        UserTask userTask = userTaskOpt.get();

        // Neuesten Content laden
        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);

        // TaskView laden
        TaskView taskView = taskViewService.findById(task.getTaskView().getId())
            .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));

        // Debug-Ausgaben für Content-Laden
        System.out.println("🔍 === DEBUG: StudentController.viewTask Content Loading ===");
        System.out.println("   - UserTask ID: " + userTask.getId());
        System.out.println("   - Task ID: " + task.getId());
        System.out.println("   - Latest content present: " + latestContent.isPresent());
        if (latestContent.isPresent()) {
            TaskContent content = latestContent.get();
            System.out.println("   - Content ID: " + content.getId());
            System.out.println("   - Content version: " + content.getVersion());
            System.out.println("   - Content length: " + (content.getContent() != null ? content.getContent().length() : "null"));
            System.out.println("   - Content preview: " + (content.getContent() != null && content.getContent().length() > 50 ? content.getContent().substring(0, 50) + "..." : content.getContent()));
        }
        System.out.println("   - Default submission: " + (task.getDefaultSubmission() != null ? task.getDefaultSubmission().substring(0, Math.min(50, task.getDefaultSubmission().length())) : "null"));

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("taskView", taskView);
        model.addAttribute("student", student);

        String contentToShow;
        if (latestContent.isPresent() && latestContent.get().getContent() != null && !latestContent.get().getContent().trim().isEmpty()) {
            contentToShow = latestContent.get().getContent();
            System.out.println("   - Using saved content: " + contentToShow.substring(0, Math.min(50, contentToShow.length())) + "...");
        } else {
            contentToShow = task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
            System.out.println("   - Using default content: " + contentToShow);
        }

        model.addAttribute("currentContent", contentToShow);
        System.out.println("🔍 === DEBUG: StudentController.viewTask Content Loading END ===");

        // Direkt das TaskView-Template zurückgeben
        return task.getTaskView().getTemplatePath();
    }

    /**
     * Task History View
     */
    @GetMapping("/tasks/{taskId}/history")
    public String taskHistory(@PathVariable Long taskId, Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/student/dashboard";
        }

        Task task = taskOpt.get();

        // Check if student has access to this task
        boolean hasAccess = task.getAssignedGroups().stream()
            .anyMatch(group -> student.getGroups().contains(group));

        if (!hasAccess) {
            return "redirect:/student/dashboard";
        }

        // Get or create UserTask
        Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
        UserTask userTask;
        if (userTaskOpt.isEmpty()) {
            // Create new UserTask if it doesn't exist
            userTask = new UserTask();
            userTask.setUser(student);
            userTask.setTask(task);
            userTask.setStartedAt(LocalDateTime.now());
            TaskStatus notStartedStatus = taskStatusRepository.findById(1L).orElse(null);
            userTask.setStatus(notStartedStatus);
            userTask = userTaskRepository.save(userTask);
        } else {
            userTask = userTaskOpt.get();
        }

        // Get all content versions
        List<TaskContent> contentVersions = taskContentService.getAllContentVersions(userTask);

        // Get all reviews for this task
        List<TaskReview> reviews = taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask);

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("contentVersions", contentVersions);
        model.addAttribute("reviews", reviews);
        model.addAttribute("student", student);

        return "student/task-history";
    }

    /**
     * View specific version of a task
     */
    @GetMapping("/tasks/{taskId}/version/{version}")
    public String viewTaskVersion(@PathVariable Long taskId, @PathVariable Integer version, 
                                Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/student/dashboard";
        }

        Task task = taskOpt.get();

        // Check if student has access to this task
        boolean hasAccess = task.getAssignedGroups().stream()
            .anyMatch(group -> student.getGroups().contains(group));

        if (!hasAccess) {
            return "redirect:/student/dashboard";
        }

        // Get UserTask
        Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
        if (userTaskOpt.isEmpty()) {
            return "redirect:/student/tasks/" + taskId + "/history";
        }

        UserTask userTask = userTaskOpt.get();
        TaskView taskView = task.getTaskView();

        // Get specific version content
        TaskContent versionContent = taskContentService.getContentByVersion(userTask, version);
        if (versionContent == null) {
            return "redirect:/student/tasks/" + taskId + "/history";
        }

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("taskView", taskView);
        model.addAttribute("student", student);
        model.addAttribute("currentContent", versionContent.getContent());
        model.addAttribute("viewingVersion", version);
        model.addAttribute("isHistoryView", true);

        // Return the appropriate task view template
        return task.getTaskView().getTemplatePath();
    }

    /**
     * Schüler-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle aktiven Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Nur aktive Aufgaben anzeigen
        List<UserTask> activeTasks = userTasks.stream()
            .filter(ut -> ut.getTask().getIsActive())
            .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("userTasks", activeTasks);

        return "student/dashboard";
    }

    private List<UserTask> getOrCreateUserTasksForStudent(User student) {
        // Gruppen des Schülers ermitteln
        Set<Group> userGroupsSet = groupService.findGroupsByUserId(student.getId());
        List<Group> userGroups = new ArrayList<>(userGroupsSet);

        // Set für eindeutige Task IDs
        Set<Long> taskIds = new HashSet<>();
        List<Task> relevantTasks = new ArrayList<>();

        // Alle aktiven Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        for (Group group : userGroups) {
            List<Task> groupTasks = taskRepository.findByAssignedGroupsContainingAndIsActiveTrue(group);
            for (Task task : groupTasks) {
                if (!taskIds.contains(task.getId())) {
                    taskIds.add(task.getId());
                    relevantTasks.add(task);
                }
            }
        }

        // Zusätzlich: Alle bereits BEGONNENEN Aufgaben des Schülers finden, 
        // auch wenn er nicht mehr in der zugewiesenen Gruppe ist
        List<UserTask> existingUserTasks = userTaskRepository.findByUser(student);
        for (UserTask existingUserTask : existingUserTasks) {
            Task task = existingUserTask.getTask();
            // Nur aktive Aufgaben berücksichtigen und nur solche, die noch nicht in der Liste sind
            if (task.getIsActive() && !taskIds.contains(task.getId())) {
                // Prüfen, ob der Schüler noch in mindestens einer der zugewiesenen Gruppen ist
                boolean isStillInAssignedGroup = false;
                for (Group assignedGroup : task.getAssignedGroups()) {
                    if (userGroups.contains(assignedGroup)) {
                        isStillInAssignedGroup = true;
                        break;
                    }
                }

                // Wenn nicht mehr in der Gruppe: Nur hinzufügen, wenn die Aufgabe wirklich begonnen wurde
                // (erkennbar an: Status ist nicht "NICHT_BEGONNEN" oder hat Submissions)
                if (isStillInAssignedGroup) {
                    // Noch in der Gruppe -> Aufgabe immer anzeigen
                    taskIds.add(task.getId());
                    relevantTasks.add(task);
                } else {
                    // Nicht mehr in der Gruppe -> nur anzeigen wenn tatsächlich begonnen
                    boolean hasReallyStarted = false;

                    // Prüfen ob Status nicht "NICHT_BEGONNEN" ist
                    if (existingUserTask.getStatus() != null && 
                        !"NICHT_BEGONNEN".equals(existingUserTask.getStatus().getName())) {
                        hasReallyStarted = true;
                    }

                    // Oder prüfen ob es Submissions gibt
                    if (!hasReallyStarted) {
                        long submissionCount = taskContentRepository.countByUserTaskAndIsSubmittedTrue(existingUserTask);
                        if (submissionCount > 0) {
                            hasReallyStarted = true;
                        }
                    }

                    if (hasReallyStarted) {
                        taskIds.add(task.getId());
                        relevantTasks.add(task);
                    }
                }
            }
        }

        List<UserTask> result = new ArrayList<>();

        // Für jede relevante Aufgabe UserTask erstellen oder finden
        for (Task task : relevantTasks) {
            Optional<UserTask> existingUserTask = userTaskRepository.findByUserAndTask(student, task);

            if (existingUserTask.isPresent()) {
                result.add(existingUserTask.get());
            } else {
                // Neue UserTask erstellen
                UserTask userTask = new UserTask();
                userTask.setUser(student);
                userTask.setTask(task);
                //userTask.setStatus(getDefaultStatus()); // getDefaultStatus() nicht vorhanden, daher entfernt
                //userTask.setCreatedAt(LocalDateTime.now()); // createdAt nicht vorhanden, daher entfernt
                //userTask.setLastModified(LocalDateTime.now()); // lastModified nicht vorhanden, daher entfernt
                TaskStatus notStartedStatus = taskStatusRepository.findById(1L).orElse(null);
                userTask.setStatus(notStartedStatus);
                userTask.setStartedAt(LocalDateTime.now());

                UserTask savedUserTask = userTaskRepository.save(userTask);
                result.add(savedUserTask);
            }
        }

        return result;
    }


    /**
     * Aufgabenliste für Schüler
     */
    @GetMapping("/tasks")
    public String taskList(Model model, Principal principal) {
       User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Gruppiere Aufgaben nach UnitTitle und sortiere sie
        Map<UnitTitle, List<UserTask>> tasksByUnitTitle = new LinkedHashMap<>();

        // Sammle alle UnitTitles und sortiere sie alphabetisch
        Set<UnitTitle> unitTitles = userTasks.stream()
            .map(ut -> ut.getTask().getUnitTitle())
            .collect(Collectors.toSet());

        List<UnitTitle> sortedUnitTitles = unitTitles.stream()
            .sorted((ut1, ut2) -> {
                // null-Werte (Aufgaben ohne Thema) kommen zuletzt
                if (ut1 == null && ut2 == null) return 0;
                if (ut1 == null) return 1;
                if (ut2 == null) return -1;
                return ut1.getName().compareTo(ut2.getName());
            })
            .collect(Collectors.toList());

        // Gruppiere Aufgaben nach UnitTitle
        for (UnitTitle unitTitle : sortedUnitTitles) {
            List<UserTask> tasksForUnit = userTasks.stream()
                .filter(ut -> Objects.equals(ut.getTask().getUnitTitle(), unitTitle))
                .sorted((ut1, ut2) -> ut1.getTask().getTitle().compareTo(ut2.getTask().getTitle()))
                .collect(Collectors.toList());

            if (!tasksForUnit.isEmpty()) {
                tasksByUnitTitle.put(unitTitle, tasksForUnit);
            }
        }

        model.addAttribute("tasksByUnitTitle", tasksByUnitTitle);
        model.addAttribute("userTasks", userTasks); // Für Rückwärtskompatibilität
        return "student/tasks-list";
    }
}