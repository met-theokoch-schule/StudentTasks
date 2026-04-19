package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TeacherDashboardQueryService;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherDashboardQueryService teacherDashboardQueryService;

    @Autowired
    private TeacherTaskCommandService teacherTaskCommandService;

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

    /**
     * Zeigt das Formular zum Löschen einer Aufgabe
     */


    /**
     * Löscht eine Aufgabe
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        try {
            teacherTaskCommandService.deleteTask(taskId, teacher);
            redirectAttributes.addFlashAttribute("success", "Aufgabe wurde erfolgreich gelöscht.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Löschen der Aufgabe: " + e.getMessage());
        }

        return "redirect:/teacher/tasks";
    }

    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task, 
                           @RequestParam(value = "selectedGroups", required = false) List<Long> selectedGroupIds,
                           @RequestParam("taskViewId") Long taskViewId,
                           @RequestParam(required = false) String unitTitleId,
                           RedirectAttributes redirectAttributes,
                           Principal principal) {

        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        task.setIsActive(true);
        teacherTaskCommandService.createTask(task, teacher, String.valueOf(taskViewId), unitTitleId, selectedGroupIds);

        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde erfolgreich erstellt.");
        return "redirect:/teacher/tasks";
    }



    /**
     * Speichert eine Aufgabe als Entwurf
     */
    @PostMapping("/tasks/draft")
    public ResponseEntity<String> saveDraft(@ModelAttribute Task task, 
                                          @RequestParam("selectedGroups") List<Long> selectedGroupIds,
                                          @RequestParam("taskViewId") Long taskViewId,
                                          Principal principal) {
        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            task.setActive(false);
            teacherTaskCommandService.createTask(task, teacher, String.valueOf(taskViewId), null, selectedGroupIds);
            return ResponseEntity.ok("Entwurf erfolgreich gespeichert");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Speichern: " + e.getMessage());
        }
    }

    @GetMapping("/teacher/submissions/{userTaskId}/view")
    public String viewSubmissionContent(@PathVariable Long userTaskId,
                                       @RequestParam(required = false) Integer version) {
        String redirectPath = "redirect:/teacher/submissions/" + userTaskId + "/view";
        if (version != null) {
            redirectPath += "?version=" + version;
        }
        return redirectPath;
    }
}
