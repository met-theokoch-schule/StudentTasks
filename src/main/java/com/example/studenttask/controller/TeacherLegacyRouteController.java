package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherLegacyRouteController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherTaskCommandService teacherTaskCommandService;

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
