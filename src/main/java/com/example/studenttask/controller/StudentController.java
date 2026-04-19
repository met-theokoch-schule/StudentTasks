package com.example.studenttask.controller;

import com.example.studenttask.dto.StudentDashboardDataDto;
import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskListDataDto;
import com.example.studenttask.dto.StudentTaskVersionViewResultDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.model.User;
import com.example.studenttask.service.StudentTaskOverviewService;
import com.example.studenttask.service.StudentTaskQueryService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskViewData(student, taskId);
        populateTaskViewModel(model, student, viewData);
        return viewData.getTaskView().getTemplatePath();
    }

    /**
     * Task History View
     */
    @GetMapping("/tasks/{taskId}/history")
    public String taskHistory(@PathVariable Long taskId, Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        Optional<StudentTaskHistoryDataDto> historyDataOpt =
            studentTaskQueryService.getTaskHistoryData(student, taskId);
        if (historyDataOpt.isEmpty()) {
            return "redirect:/student/dashboard";
        }

        StudentTaskHistoryDataDto historyData = historyDataOpt.get();
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
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        StudentTaskVersionViewResultDto result =
            studentTaskQueryService.getTaskVersionViewData(student, taskId, version);
        if (result.isRedirect()) {
            return result.getRedirectPath();
        }

        StudentTaskViewDataDto viewData = result.getViewData();
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
        User currentUser = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

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
       User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        StudentTaskListDataDto taskListData = studentTaskOverviewService.getTaskListData(student);

        model.addAttribute("tasksByUnitTitle", taskListData.getTasksByUnitTitle());
        model.addAttribute("userTasks", taskListData.getUserTasks());
        return "student/tasks-list";
    }

    private void populateTaskViewModel(Model model, User student, StudentTaskViewDataDto viewData) {
        model.addAttribute("task", viewData.getTask());
        model.addAttribute("userTask", viewData.getUserTask());
        model.addAttribute("taskView", viewData.getTaskView());
        model.addAttribute("student", student);
        model.addAttribute("currentContent", viewData.getCurrentContent());
    }
}
