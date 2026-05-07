package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.TeacherTaskQueryService;
import com.example.studenttask.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

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
        User teacher = requireTeacher(principal.getName());

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
        User teacher = requireTeacher(principal.getName());

        TeacherTaskSubmissionsDataDto submissionsData = teacherTaskQueryService.getTaskSubmissionsData(taskId, teacher)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabe nicht gefunden"));

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
                             @RequestParam(required = false) String returnUrl,
                             @RequestParam(value = "continueToNext", defaultValue = "false") boolean continueToNext,
                             Authentication authentication,
                             HttpServletRequest request) {
        User teacher = continueToNext ? requireTeacher(authentication.getName()) : null;

        teacherTaskCommandService.submitReview(
            userTaskId,
            authentication.getName(),
            statusId,
            comment,
            request.getParameter("currentVersion")
        );

        if (continueToNext) {
            Optional<UserTask> nextReview = teacherTaskQueryService.findNextReviewForTask(userTaskId, teacher);
            if (nextReview.isPresent()) {
                return redirectToSubmissionReview(nextReview.get().getId(), returnUrl);
            }
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
        TeacherSubmissionContentViewDto contentViewData =
            teacherTaskQueryService.getSubmissionContentViewData(userTaskId, version)
                .orElseThrow(() -> new TeacherResourceNotFoundException("Abgabe nicht gefunden"));

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
        model.addAttribute("taskForm", formData.getTaskForm());
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());
        return "teacher/task-create";
    }

    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute("taskForm") TeacherTaskFormDto taskForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {
        validateTaskForm(taskForm, bindingResult, true);
        if (bindingResult.hasErrors()) {
            populateCreateTaskFormModel(model, taskForm);
            return "teacher/task-create";
        }

        User teacher = requireTeacher(authentication.getName());
        teacherTaskCommandService.createTask(teacher, taskForm);

        return "redirect:/teacher/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        TeacherTaskFormDataDto formData = teacherTaskQueryService.getEditTaskFormData(id)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabe nicht gefunden"));
        model.addAttribute("task", formData.getTask());
        model.addAttribute("taskForm", formData.getTaskForm());
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());

        return "teacher/task-edit";
    }

    @PostMapping("/tasks/{id}/edit")
    public String updateTask(@PathVariable Long id,
            @Valid @ModelAttribute("taskForm") TeacherTaskFormDto taskForm,
            BindingResult bindingResult,
            Model model) {
        validateTaskForm(taskForm, bindingResult, false);
        if (bindingResult.hasErrors()) {
            populateEditTaskFormModel(id, model, taskForm);
            return "teacher/task-edit";
        }

        teacherTaskCommandService.updateTask(id, taskForm);
        return "redirect:/teacher/tasks";
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id, Authentication authentication) {
        User teacher = requireTeacher(authentication.getName());
        teacherTaskCommandService.deleteTask(id, teacher);

        return "redirect:/teacher/tasks";
    }


    @GetMapping("/submissions/{userTaskId}")
    public String reviewSubmission(@PathVariable Long userTaskId,
                                 @RequestParam(required = false) String returnUrl,
                                 Model model,
                                 Principal principal,
                                 HttpServletRequest request) {
        User teacher = requireTeacher(principal.getName());
        TeacherSubmissionReviewDataDto reviewData = teacherTaskQueryService.getSubmissionReviewData(userTaskId, teacher)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Abgabe nicht gefunden"));
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
        model.addAttribute("nextReviewUserTask", reviewData.getNextReviewUserTask());
        model.addAttribute("hasNextReview", reviewData.getNextReviewUserTask() != null);

        return "teacher/submission-review";
    }

    private String redirectToSubmissionReview(Long userTaskId, String returnUrl) {
        UriComponentsBuilder redirectBuilder = UriComponentsBuilder
            .fromPath("/teacher/submissions/{userTaskId}");

        if (returnUrl != null && !returnUrl.trim().isEmpty()) {
            redirectBuilder.queryParam("returnUrl", returnUrl);
        }

        return "redirect:" + redirectBuilder.buildAndExpand(userTaskId).encode().toUriString();
    }

    private String buildCurrentUrl(HttpServletRequest request) {
        String currentUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }
        return currentUrl;
    }

    private void validateTaskForm(
            TeacherTaskFormDto taskForm,
            BindingResult bindingResult,
            boolean requireTaskView) {
        if (requireTaskView && taskForm.getTaskViewId() == null) {
            bindingResult.rejectValue("taskViewId", "required", "Bitte wählen Sie einen Aufgabentyp aus.");
        } else if (taskForm.getTaskViewId() != null
                && !teacherTaskQueryService.hasTaskView(taskForm.getTaskViewId())) {
            bindingResult.rejectValue("taskViewId", "notFound", "Der gewählte Aufgabentyp existiert nicht.");
        }

        if (taskForm.getUnitTitleId() != null
                && !taskForm.getUnitTitleId().trim().isEmpty()
                && !teacherTaskQueryService.hasUnitTitle(taskForm.getUnitTitleId())) {
            bindingResult.rejectValue("unitTitleId", "notFound", "Das gewählte Thema existiert nicht.");
        }

        if (!teacherTaskQueryService.hasAllGroups(taskForm.getSelectedGroups())) {
            bindingResult.rejectValue("selectedGroups", "notFound", "Eine oder mehrere gewählte Gruppen existieren nicht.");
        }
    }

    private void populateCreateTaskFormModel(Model model, TeacherTaskFormDto taskForm) {
        TeacherTaskFormDataDto formData = teacherTaskQueryService.getCreateTaskFormData();
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());
    }

    private void populateEditTaskFormModel(Long id, Model model, TeacherTaskFormDto taskForm) {
        TeacherTaskFormDataDto formData = teacherTaskQueryService.getEditTaskFormData(id)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabe nicht gefunden"));
        model.addAttribute("task", formData.getTask());
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("taskViews", formData.getTaskViews());
        model.addAttribute("groups", formData.getGroups());
        model.addAttribute("unitTitles", formData.getUnitTitles());
    }

    private User requireTeacher(String openIdSubject) {
        return userService.findByOpenIdSubject(openIdSubject)
            .orElseThrow(() -> new TeacherAuthenticationRequiredException("Benutzer nicht gefunden"));
    }
}
