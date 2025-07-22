package com.example.studenttask.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;

@Controller
@RequestMapping("/teacher")
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
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        System.out.println("🔍 === DEBUG: Task List Loading ===");
        System.out.println("   - Loading tasks for teacher: " + teacher.getName() + " (ID: " + teacher.getId() + ")");

        List<Task> tasks = taskService.findByCreatedBy(teacher);
        System.out.println("   - Found " + tasks.size() + " tasks in task list");
        for (Task task : tasks) {
            System.out.println("   - Task: " + task.getTitle() + " (ID: " + task.getId() + ", Active: "
                    + task.getIsActive() + ")");
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("tasks", tasks);

        return "teacher/tasks-list";
    }

    /**
     * Zeigt die Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        Optional<Task> taskOpt = taskService.findById(taskId);

        // Sicherheit: Prüfen ob Aufgabe existiert und dem Lehrer gehört
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();

        List<UserTask> userTasks = userTaskService.findByTask(task);

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("userTasks", userTasks);

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

    @GetMapping("/submissions/{userTaskId}")
    public String viewSubmission(@PathVariable Long userTaskId, Model model, Authentication authentication) {
        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }
        UserTask userTask = userTaskOpt.get();
        model.addAttribute("userTask", userTask);

        // Get all submissions for this user task (version history)
        List<TaskContent> submissions = taskContentService.getAllContentVersions(userTask);
        model.addAttribute("submissions", submissions);

        // Get all reviews for this user task
        List<TaskReview> reviews = taskReviewService.findByUserTask(userTask);
        model.addAttribute("reviews", reviews);

        // Get available statuses for review
        List<TaskStatus> statuses = taskStatusService.findAllActive();
        model.addAttribute("statuses", statuses);

        return "teacher/submission-review";
    }

    @PostMapping("/submissions/{userTaskId}/review")
    public String submitReview(@PathVariable Long userTaskId,
                             @RequestParam Long statusId,
                             @RequestParam(required = false) String comment,
                             @RequestParam(required = false) String submissionIdStr,
                             Authentication authentication) {

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
            @RequestParam List<Long> selectedGroups,
            Authentication authentication) {

        User teacher = userService.findByOpenIdSubject(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        task.setCreatedBy(teacher);

        // Assign selected groups to the task
        List<Group> groups = groupService.findAllById(selectedGroups);
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
            @RequestParam List<Long> selectedGroups,
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
        List<Group> groups = groupService.findAllById(selectedGroups);
        existingTask.setAssignedGroups(new HashSet<>(groups));

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
}