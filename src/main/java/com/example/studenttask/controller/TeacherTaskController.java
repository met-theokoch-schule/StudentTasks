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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.Theme;
import com.example.studenttask.service.ThemeService;
import com.example.studenttask.service.GroupService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.studenttask.model.Group;


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
    private ThemeService themeService;
    
    @Autowired
    private GroupService groupService;


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
            System.out.println("   - Task: " + task.getTitle() + " (ID: " + task.getId() + ", Active: " + task.getIsActive() + ")");
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
                             @RequestParam(required = false) Long submissionId,
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

        // Create review
        taskReviewService.createReview(userTask, reviewer, statusId, comment, submissionId);

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
        String templatePath = task.getTaskView() != null ? 
            task.getTaskView().getTemplatePath() : 
            "taskviews/simple-text.html";

        return templatePath;
    }
    
    @GetMapping("/tasks/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("taskViews", taskViewService.findActiveTaskViews());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("themes", themeService.findActiveThemes());
        return "teacher/task-create";
    }

    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task, 
                           @RequestParam(required = false) Long taskViewId,
                           @RequestParam(required = false) Long themeId,
                           @RequestParam(required = false) List<Long> selectedGroups,
                           RedirectAttributes redirectAttributes) {

        User teacher = userService.findByOpenIdSubject(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Lehrer nicht gefunden"));
        task.setCreatedBy(teacher);
        task.setIsActive(true);

        // Set TaskView if provided
        if (taskViewId != null) {
            TaskView taskView = taskViewService.findById(taskViewId).orElse(null);
            task.setTaskView(taskView);
        }

        // Set Theme if provided
        if (themeId != null) {
            Theme theme = themeService.findById(themeId).orElse(null);
            task.setTheme(theme);
        }

        taskService.save(task);

        // Assign task to selected groups
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            for (Long groupId : selectedGroups) {
                Group group = groupService.findById(groupId).orElse(null);
                if (group != null) {
                    userTaskService.createTasksForGroup(task, group);
                    redirectAttributes.addFlashAttribute("message", "Aufgabe erfolgreich erstellt und an Gruppe verteilt.");
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Aufgabe erfolgreich erstellt.");
        }

        return "redirect:/teacher/tasks";
    }
}