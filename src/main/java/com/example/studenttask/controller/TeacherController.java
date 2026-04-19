package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TeacherDashboardQueryService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherDashboardQueryService teacherDashboardQueryService;

    /**
     * Lehrer-Dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
    public String dashboard(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName()).orElse(null);
        if (teacher == null) {
            return "redirect:/login";
        }

        TeacherDashboardDataDto dashboardData = teacherDashboardQueryService.getDashboardData(teacher);
        model.addAttribute("pendingReviews", dashboardData.getPendingReviews());
        model.addAttribute("recentTasks", dashboardData.getRecentTasks());

        return "teacher/dashboard";
    }

    @GetMapping("/reviews/pending")
    public String pendingReviews(Model model, Authentication authentication) {
        User teacher = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
        if (teacher == null) {
            return "redirect:/home";
        }

        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews =
            teacherDashboardQueryService.getGroupedPendingReviews(teacher);

        model.addAttribute("groupedPendingReviews", groupedPendingReviews);
        return "teacher/pending-reviews";
    }
}
