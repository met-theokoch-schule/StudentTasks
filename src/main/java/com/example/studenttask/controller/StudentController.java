package com.example.studenttask.controller;

import com.example.studenttask.dto.StudentDashboardDataDto;
import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskListDataDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.User;
import com.example.studenttask.service.StudentTaskOverviewService;
import com.example.studenttask.service.StudentTaskQueryService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentTaskOverviewService studentTaskOverviewService;

    @Autowired
    private StudentTaskQueryService studentTaskQueryService;

    /**
     * Direkte Aufgaben-Ansicht (ohne task-edit.html Wrapper)
     */
    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable Long taskId, Model model, Principal principal) {
        User student = requireStudent(principal.getName());
        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskViewData(student, taskId);
        populateTaskViewModel(model, student, viewData);
        return viewData.getTaskView().getTemplatePath();
    }

    /**
     * Task History View
     */
    @GetMapping("/tasks/{taskId}/history")
    public String taskHistory(@PathVariable Long taskId, Model model, Principal principal) {
        User student = requireStudent(principal.getName());
        StudentTaskHistoryDataDto historyData = studentTaskQueryService.getTaskHistoryData(student, taskId);
        model.addAttribute("task", historyData.getTask());
        model.addAttribute("userTask", historyData.getUserTask());
        model.addAttribute("contentVersions", historyData.getContentVersions());
        model.addAttribute("reviews", historyData.getReviews());
        model.addAttribute("student", student);

        return "student/task-history";
    }

    /**
     * View specific version of a task
     */
    @GetMapping("/tasks/{taskId}/version/{version}")
    public String viewTaskVersion(@PathVariable Long taskId, @PathVariable Integer version, 
                                Model model, Principal principal) {
        User student = requireStudent(principal.getName());
        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskVersionViewData(student, taskId, version);
        populateTaskViewModel(model, student, viewData);
        model.addAttribute("viewingVersion", viewData.getViewingVersion());
        model.addAttribute("isHistoryView", viewData.isHistoryView());
        return viewData.getTaskView().getTemplatePath();
    }

    /**
     * Schüler-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User currentUser = requireStudent(principal.getName());

        StudentDashboardDataDto dashboardData = studentTaskOverviewService.getDashboardData(currentUser);

        model.addAttribute("student", currentUser);
        model.addAttribute("userTasks", dashboardData.getRecentUserTasks());
        model.addAttribute("totalTaskCount", dashboardData.getTotalTaskCount());
        model.addAttribute("inProgress", dashboardData.getInProgress());
        model.addAttribute("pendingReview", dashboardData.getPendingReview());
        model.addAttribute("needsRework", dashboardData.getNeedsRework());
        model.addAttribute("completed", dashboardData.getCompleted());

        return "student/dashboard";
    }

    /**
     * Aufgabenliste für Schüler
     */
    @GetMapping("/tasks")
    public String taskList(Model model, Principal principal) {
       User student = requireStudent(principal.getName());
        StudentTaskListDataDto taskListData = studentTaskOverviewService.getTaskListData(student);

        model.addAttribute("tasksByUnitTitle", taskListData.getTasksByUnitTitle());
        model.addAttribute("userTasks", taskListData.getUserTasks());
        model.addAttribute("expandedUnitIds", taskListData.getExpandedUnitIds());
        return "student/tasks-list";
    }

    private void populateTaskViewModel(Model model, User student, StudentTaskViewDataDto viewData) {
        model.addAttribute("task", viewData.getTask());
        model.addAttribute("userTask", viewData.getUserTask());
        model.addAttribute("taskView", viewData.getTaskView());
        model.addAttribute("student", student);
        model.addAttribute("currentContent", viewData.getCurrentContent());
    }

    private User requireStudent(String openIdSubject) {
        return userService.findByOpenIdSubject(openIdSubject)
            .orElseThrow(() -> new UserAuthenticationRequiredException("Benutzer nicht gefunden"));
    }
}
