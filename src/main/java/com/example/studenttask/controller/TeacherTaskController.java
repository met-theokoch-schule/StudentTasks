package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.TeacherTaskQueryService;
import com.example.studenttask.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherTaskController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherTaskQueryService teacherTaskQueryService;

    @Autowired
    private TeacherTaskCommandService teacherTaskCommandService;

    /**
     * Zeigt alle Aufgaben des eingeloggten Lehrers oder alle Aufgaben im System
     */
    @GetMapping("/tasks")
    public String listTasks(@RequestParam(value = "filter", defaultValue = "own") String filter,
                           Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        TeacherTaskListDataDto taskListData = teacherTaskQueryService.getTaskListData(teacher, filter);

        model.addAttribute("teacher", teacher);
        model.addAttribute("tasksByUnitTitle", taskListData.getTasksByUnitTitle());
        model.addAttribute("tasks", taskListData.getTasks());
        model.addAttribute("currentFilter", filter);
        return "teacher/tasks-list";
    }

    /**
     * Zeigt die Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal, HttpServletRequest request) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<TeacherTaskSubmissionsDataDto> submissionsDataOpt =
            teacherTaskQueryService.getTaskSubmissionsData(taskId, teacher);
        if (submissionsDataOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        TeacherTaskSubmissionsDataDto submissionsData = submissionsDataOpt.get();

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", submissionsData.getTask());
        model.addAttribute("userTasks", submissionsData.getUserTasks());
        model.addAttribute("currentUrl", buildCurrentUrl(request));
        model.addAttribute("isOwnTask", submissionsData.isOwnTask());

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
        boolean submitted = teacherTaskCommandService.submitReview(
            userTaskId,
            authentication.getName(),
            statusId,
            comment,
            submissionIdStr,
            request.getParameter("currentVersion")
        );
        if (!submitted) {
            return "redirect:/teacher/tasks";
        }

        // Redirect back to the original page if returnUrl is provided
        if (returnUrl != null && !returnUrl.trim().isEmpty()) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/teacher/submissions/" + userTaskId;
    }

    @GetMapping("/submissions/{userTaskId}/view")
    public String viewSubmissionInTaskView(@PathVariable Long userTaskId,
            @RequestParam(required = false) Integer version,
            Model model) {
        Optional<TeacherSubmissionContentViewDto> contentViewDataOpt =
            teacherTaskQueryService.getSubmissionContentViewData(userTaskId, version);
        if (contentViewDataOpt.isEmpty()) {
            return "error/404";
        }

        TeacherSubmissionContentViewDto contentViewData = contentViewDataOpt.get();

        model.addAttribute("task", contentViewData.getTask());
        model.addAttribute("userTask", contentViewData.getUserTask());
        model.addAttribute("userTaskId", contentViewData.getUserTask().getId());
        model.addAttribute("currentContent", contentViewData.getCurrentContent());
        model.addAttribute("isTeacherView", true);
        model.addAttribute("version", contentViewData.getVersion());

        return contentViewData.getTemplatePath();
    }

    @GetMapping("/tasks/create")
    public String showCreateTaskForm(Model model) {
        TeacherTaskFormDataDto formData = teacherTaskQueryService.getCreateTaskFormData();
        model.addAttribute("task", formData.getTask());
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());
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
        teacherTaskCommandService.createTask(task, teacher, taskViewId, unitTitleId, selectedGroups);

        return "redirect:/teacher/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Optional<TeacherTaskFormDataDto> formDataOpt = teacherTaskQueryService.getEditTaskFormData(id);
        if (formDataOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        TeacherTaskFormDataDto formData = formDataOpt.get();
        model.addAttribute("task", formData.getTask());
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());

        return "teacher/task-edit";
    }

    @PostMapping("/tasks/{id}/edit")
    public String updateTask(@PathVariable Long id,
            @ModelAttribute Task task,
            @RequestParam(required = false) List<Long> selectedGroups,
            @RequestParam String taskViewId,
            @RequestParam String unitTitleId,
            @RequestParam(required = false) String tutorial) {
        teacherTaskCommandService.updateTask(id, task, taskViewId, unitTitleId, selectedGroups, tutorial);
        return "redirect:/teacher/tasks";
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id, Authentication authentication) {
        User teacher = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
        if (teacher == null) {
            return "redirect:/teacher/tasks";
        }

        try {
            teacherTaskCommandService.deleteTask(id, teacher);
        } catch (RuntimeException e) {
            return "redirect:/teacher/tasks";
        }

        return "redirect:/teacher/tasks";
    }


    @GetMapping("/submissions/{userTaskId}")
    public String reviewSubmission(@PathVariable Long userTaskId,
                                 @RequestParam(required = false) String returnUrl,
                                 Model model,
                                 HttpServletRequest request) {
        Optional<TeacherSubmissionReviewDataDto> reviewDataOpt =
            teacherTaskQueryService.getSubmissionReviewData(userTaskId);
        if (reviewDataOpt.isEmpty()) {
            return "redirect:/teacher/tasks";
        }

        TeacherSubmissionReviewDataDto reviewData = reviewDataOpt.get();
        model.addAttribute("userTask", reviewData.getUserTask());

        // If no returnUrl is provided as parameter, get it from the HTTP Referer header
        String finalReturnUrl = returnUrl;
        if (finalReturnUrl == null || finalReturnUrl.trim().isEmpty()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                finalReturnUrl = referer;
            }
        }
        model.addAttribute("returnUrl", finalReturnUrl);
        model.addAttribute("reviews", reviewData.getReviews());
        model.addAttribute("statuses", reviewData.getStatuses());
        model.addAttribute("versionsWithStatus", reviewData.getVersionsWithStatus());

        return "teacher/submission-review";
    }

    private String buildCurrentUrl(HttpServletRequest request) {
        String currentUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }
        return currentUrl;
    }
}
