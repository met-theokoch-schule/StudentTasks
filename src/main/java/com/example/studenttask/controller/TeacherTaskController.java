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

        List<UserTask> submissions = userTaskService.findByTask(task);

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);

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
        List<TaskContent> submissions = taskContentService.findByUserTaskOrderByVersionDesc(userTask);
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
}