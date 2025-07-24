package com.example.studenttask.controller;

import com.example.studenttask.dto.VersionWithSubmissionStatus;
import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskReviewService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskStatusService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UnitTitleService;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.UnitTitle;
import java.security.Principal;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private TaskReviewService taskReviewService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UnitTitleService unitTitleService;

    /**
     * Zeigt die Übersicht aller Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Authentication authentication) {
        User teacher = userService.findByOpenIdSubject(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        System.out.println("🔍 === DEBUG: Task List Loading ===");
        System.out.println("   - Loading tasks for teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");

        List<Task> allTasks = taskService.findByCreatedBy(teacher);

        System.out.println("   - Found " + allTasks.size() + " tasks in task list");

        // Gruppiere Aufgaben nach UnitTitle, ähnlich wie in der Schüler-Ansicht
        Map<UnitTitle, List<Task>> tasksByUnitTitle = new LinkedHashMap<>();

        // Sammle alle UnitTitles und sortiere sie alphabetisch
        Set<UnitTitle> unitTitles = allTasks.stream()
            .map(Task::getUnitTitle)
            .collect(Collectors.toSet());

        List<UnitTitle> sortedUnitTitles = unitTitles.stream()
            .sorted((ut1, ut2) -> {
                // null-Werte (Aufgaben ohne Thema) kommen zuletzt
                if (ut1 == null && ut2 == null) return 0;
                if (ut1 == null) return 1;
                if (ut2 == null) return -1;
                // Sortierung nach weight (aufsteigend), dann nach Name
                int weightComparison = Integer.compare(ut1.getWeight(), ut2.getWeight());
                if (weightComparison != 0) {
                    return weightComparison;
                }
                return ut1.getName().compareTo(ut2.getName());
            })
            .collect(Collectors.toList());

        // Gruppiere Aufgaben nach UnitTitle
        for (UnitTitle unitTitle : sortedUnitTitles) {
            List<Task> tasksForUnit = allTasks.stream()
                .filter(task -> Objects.equals(task.getUnitTitle(), unitTitle))
                .sorted((t1, t2) -> t1.getTitle().compareTo(t2.getTitle()))
                .collect(Collectors.toList());

            if (!tasksForUnit.isEmpty()) {
                tasksByUnitTitle.put(unitTitle, tasksForUnit);
            }
        }

        model.addAttribute("tasksByUnitTitle", tasksByUnitTitle);
        model.addAttribute("tasks", allTasks); // Für Rückwärtskompatibilität
        return "teacher/tasks-list";
    }

    /**
     * Zeigt die Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal, HttpServletRequest request) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        Optional<Task> taskOpt = taskService.findById(taskId);

        // Sicherheit: Prüfen ob Aufgabe existiert und dem Lehrer gehört
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();

        List<UserTask> userTasks = userTaskService.findByTask(task);

        // Current URL für returnUrl
        String currentUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("userTasks", userTasks);
        model.addAttribute("currentUrl", currentUrl);

        return "teacher/task-submissions";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Task> taskOpt = taskService.findById(id);
        if (taskOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();
        model.addAttribute("task", task);

        // Get all user tasks for this task with submissions
        List<UserTask> userTasks = userTaskService.findByTask(task);
        model.addAttribute("userTasks", userTasks);

        return "teacher/task-submissions";
    }



    @PostMapping("/submissions/{userTaskId}/review")
    public String submitReview(@PathVariable Long userTaskId,
                             @RequestParam Long statusId,
                             @RequestParam(required = false) String comment,
                             @RequestParam(required = false) String submissionIdStr,
                             @RequestParam(required = false) String returnUrl,
                             Authentication authentication,
                             HttpServletRequest request) {

        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }
        UserTask userTask = userTaskOpt.get();
        User reviewer = userService.findByOpenIdSubject(authentication.getName()).orElse(null);

        if (reviewer == null) {
            return "redirect:/teacher/tasks";
        }

        Long submissionId = null;
        if (submissionIdStr != null && !submissionIdStr.isEmpty()) {
            submissionId = Long.parseLong(submissionIdStr);
        }

        // Create review with version information
        Integer currentVersion = null;
        if (request.getParameter("currentVersion") != null && !request.getParameter("currentVersion").isEmpty()) {
            currentVersion = Integer.parseInt(request.getParameter("currentVersion"));
        }

        taskReviewService.createReview(userTask, reviewer, statusId, comment, submissionId, currentVersion);

        // Explicitly save the UserTask to ensure status change is persisted
        userTaskService.save(userTask);

        // Redirect back to the original page if returnUrl is provided
        if (returnUrl != null && !returnUrl.trim().isEmpty()) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/teacher/submissions/" + userTaskId;
    }

    @GetMapping("/submissions/{userTaskId}/view")
    public String viewSubmissionInTaskView(@PathVariable Long userTaskId,
            @RequestParam(required = false) Integer version,
            Model model,
            Authentication authentication) {
        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return "error/404";
        }
        UserTask userTask = userTaskOpt.get();
        Task task = userTask.getTask();

        // Get the specific version or latest version
        TaskContent content;
        if (version != null) {
            content = taskContentService.getContentByVersion(userTask, version);
        } else {
            List<TaskContent> contents = taskContentService.getAllContentVersions(userTask);
            content = contents.isEmpty() ? null : contents.get(0);
        }

        // If no content found, create default content
        if (content == null) {
            content = new TaskContent();
            content.setContent(task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");
            content.setVersion(1);
        }

        // Add model attributes for task view
        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("currentContent", content.getContent());
        model.addAttribute("isTeacherView", true);
        model.addAttribute("version", content.getVersion());

        // Determine the template path
        String templatePath = task.getTaskView() != null ? task.getTaskView().getTemplatePath()
                : "taskviews/simple-text.html";

        return templatePath;
    }

    @GetMapping("/tasks/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("taskViews", taskViewService.findAllActive());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("unitTitles", unitTitleService.findAllActive());
        return "teacher/task-create";
    }

    @PostMapping("/tasks")
    public String createTask(@ModelAttribute Task task,
            @RequestParam String taskViewId,
            @RequestParam(required = false) String unitTitleId,
            @RequestParam(required = false) List<Long> selectedGroups,
            Authentication authentication) {

        User teacher = userService.findByOpenIdSubject(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        task.setCreatedBy(teacher);

        // Assign selected groups to the task
        List<Group> groups = new ArrayList<>();
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            groups = groupService.findAllById(selectedGroups);
        }
        task.setAssignedGroups(new HashSet<>(groups));

        // Set task view
        try {
            Optional<TaskView> taskViewOpt = taskViewService.findById(Long.parseLong(taskViewId));
            if (taskViewOpt.isPresent()) {
                task.setTaskView(taskViewOpt.get());
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid unitTitleId format: " + unitTitleId);
        }

        // Set unit title
        UnitTitle unitTitle = null;
        if (unitTitleId != null && !unitTitleId.trim().isEmpty()) {

            unitTitle = unitTitleService.findById(unitTitleId);

        }

        taskService.save(task);

        return "redirect:/teacher/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        List<TaskView> taskViews = taskViewService.findActiveTaskViews();
        List<Group> allGroups = groupService.findAll();
        List<UnitTitle> unitTitles = unitTitleService.findAllActive();

        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews != null ? taskViews : new ArrayList<>());
        model.addAttribute("groups", allGroups);
        model.addAttribute("unitTitles", unitTitles);
        model.addAttribute("teacher", teacher);

        return "teacher/task-edit";
    }

    @PostMapping("/tasks/{id}/edit")
    public String updateTask(@PathVariable Long id,
            @ModelAttribute Task task,
            @RequestParam(required = false) List<Long> selectedGroups,
            @RequestParam String taskViewId,
            @RequestParam String unitTitleId,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        Task existingTask = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        // Update basic task information
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDefaultSubmission(task.getDefaultSubmission());
        existingTask.setIsActive(task.getIsActive());

        // Update assigned groups
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            List<Group> groups = groupService.findAllById(selectedGroups);
            existingTask.setAssignedGroups(new HashSet<>(groups));
        } else {
            existingTask.setAssignedGroups(new HashSet<>());
        }

        // Update task view
        try {
            Optional<TaskView> taskViewOpt = taskViewService.findById(Long.parseLong(taskViewId));
            if (taskViewOpt.isPresent()) {
                existingTask.setTaskView(taskViewOpt.get());
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid unitTitleId format: " + unitTitleId);
            existingTask.setUnitTitle(null);
        }

        // Update unit title
        if (unitTitleId != null && !unitTitleId.trim().isEmpty()) {

            UnitTitle unitTitle = unitTitleService.findById(unitTitleId);
            existingTask.setUnitTitle(unitTitle);

        } else {
            existingTask.setUnitTitle(null);
        }

        taskService.save(existingTask);
        return "redirect:/teacher/tasks";
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        taskService.delete(task);
        return "redirect:/teacher/tasks";
    }


@GetMapping("/submissions/{userTaskId}")
    public String reviewSubmission(@PathVariable Long userTaskId, 
                                 @RequestParam(required = false) String returnUrl,
                                 Model model, Authentication authentication,
                                 HttpServletRequest request) {
        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        UserTask userTask = userTaskOpt.get();
        model.addAttribute("userTask", userTask);

        // If no returnUrl is provided as parameter, get it from the HTTP Referer header
        String finalReturnUrl = returnUrl;
        if (finalReturnUrl == null || finalReturnUrl.trim().isEmpty()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                finalReturnUrl = referer;
            }
        }
        model.addAttribute("returnUrl", finalReturnUrl);

        // Get all reviews for this user task
        List<TaskReview> reviews = taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask);
        model.addAttribute("reviews", reviews);

        // Get available statuses for teacher reviews
        List<TaskStatus> statuses = taskReviewService.getTeacherReviewStatuses();
        model.addAttribute("statuses", statuses);

        // Get all task contents with submission status
        List<TaskContent> taskContents = taskContentService.getAllContentVersions(userTask);
        List<VersionWithSubmissionStatus> versionsWithStatus = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        for (TaskContent content : taskContents) {
            LocalDateTime submissionDateTime = content.getSavedAt();
            String formattedDateTime = submissionDateTime != null ? submissionDateTime.format(formatter) : "Unbekanntes Datum";
            String displayText = "v" + content.getVersion() + " " + formattedDateTime;

            if (content.isSubmitted()) {
                long reviewCount = taskReviewService.countReviewsForVersion(userTask, content.getVersion());
                String statusIcon = reviewCount > 0 ? "\uD83D\uDC41" : "\u23F3"; // Auge : Sanduhr
                displayText += " " + statusIcon;
            } else {
                LocalDateTime updateDateTime = content.getSavedAt();
                String formattedUpdateDateTime = updateDateTime != null ? updateDateTime.format(formatter) : "Unbekanntes Datum";
                displayText = "v" + content.getVersion() + " " + formattedUpdateDateTime;
            }
            versionsWithStatus.add(new VersionWithSubmissionStatus(content.getVersion(), content.isSubmitted(), displayText));
        }

        model.addAttribute("versionsWithStatus", versionsWithStatus);

        return "teacher/submission-review";
    }
}