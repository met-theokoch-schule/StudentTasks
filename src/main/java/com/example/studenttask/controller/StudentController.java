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
import com.example.studenttask.repository.*;
import com.example.studenttask.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

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

        // Alle Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Aktive Aufgaben und bereits begonnene inaktive Aufgaben anzeigen
        // (UserTasks werden nur erstellt wenn der Schüler Zugang zur Aufgabe hat/hatte)
        List<UserTask> visibleTasks = userTasks;

        model.addAttribute("student", student);
        model.addAttribute("userTasks", visibleTasks);

        return "student/dashboard";
    }

    private List<UserTask> getOrCreateUserTasksForStudent(User student) {
        System.out.println("🔍 === DEBUG: Getting UserTasks for student: " + student.getName() + " (ID: " + student.getId() + ") ===");

        List<UserTask> allUserTasks = new ArrayList<>();

        // Hole alle Gruppen des Benutzers
        Set<Group> userGroups = student.getGroups();
        System.out.println("   - Student is member of " + userGroups.size() + " groups");

        for (Group group : userGroups) {
            System.out.println("   - Checking group: " + group.getName() + " (ID: " + group.getId() + ")");

            // Finde alle aktiven Aufgaben, die dieser Gruppe zugewiesen sind
            List<Task> groupTasks = taskRepository.findByIsActiveTrueAndAssignedGroupsContainsOrderByCreatedAtDesc(group)
                .stream()
                .filter(task -> task.getAssignedGroups().contains(group))
                .collect(Collectors.toList());

            System.out.println("     - Found " + groupTasks.size() + " tasks assigned to this group");

            for (Task task : groupTasks) {
                System.out.println("     - Processing task: " + task.getTitle() + " (ID: " + task.getId() + ")");

                // Prüfe, ob bereits ein UserTask existiert
                Optional<UserTask> existingUserTask = userTaskRepository.findByUserAndTask(student, task);

                if (existingUserTask.isPresent()) {
                    System.out.println("       - UserTask already exists (ID: " + existingUserTask.get().getId() + ")");
                    allUserTasks.add(existingUserTask.get());
                } else {
                    System.out.println("       - Creating new UserTask");
                    // Erstelle einen neuen UserTask
                    UserTask newUserTask = new UserTask();
                    newUserTask.setUser(student);
                    newUserTask.setTask(task);
                    newUserTask.setStartedAt(LocalDateTime.now());
                    // Status auf "NOT_STARTED" setzen (ID: 1)
                    TaskStatus notStartedStatus = taskStatusRepository.findById(1L).orElse(null);
                    newUserTask.setStatus(notStartedStatus);

                    userTaskRepository.save(newUserTask);
                    System.out.println("       - UserTask created with ID: " + newUserTask.getId());
                    allUserTasks.add(newUserTask);
                }
            }
        }

        System.out.println("🔍 === DEBUG: Total UserTasks found/created: " + allUserTasks.size() + " ===");
        return allUserTasks;
    }


    /**
     * Aufgabenliste für Schüler
     */
    @GetMapping("/tasks")
    public String taskList(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Aktive Aufgaben und bereits begonnene inaktive Aufgaben anzeigen
        // (UserTasks werden nur erstellt wenn der Schüler Zugang zur Aufgabe hat/hatte)
        List<UserTask> visibleTasks = userTasks;

        model.addAttribute("student", student);
        model.addAttribute("userTasks", visibleTasks);

        return "student/tasks-list";
    }
}