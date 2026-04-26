package com.example.studenttask.controller;

import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.AuthenticationService;
import com.example.studenttask.service.StudentTaskViewSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Debug Controller - nur für Entwicklungszwecke
 * Ermöglicht das Anzeigen von Aufgabeninhalten für den aktuell eingeloggten Benutzer
 * Komplett unabhängig vom Rest des Systems
 */
@Controller
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Zeigt den gespeicherten Inhalt einer Submission für den aktuellen Benutzer an
     * URL: /debug/content/{taskId}?version={versionNumber}
     */
    @GetMapping("/content/{taskId}")
    public String viewSubmissionContent(
            @PathVariable Long taskId,
            @RequestParam(required = false) Integer version,
            Model model) {
        User currentUser = authenticationService.getCurrentUser()
            .orElseThrow(() -> new UserAuthenticationRequiredException("Benutzer nicht gefunden"));

        Task task = studentTaskViewSupportService.findAssignedTask(currentUser, taskId)
            .orElseThrow(() -> new StudentResourceNotFoundException("Aufgabe nicht gefunden"));

        UserTask userTask = studentTaskViewSupportService.findExistingUserTask(currentUser, task)
            .orElseThrow(() -> new StudentResourceNotFoundException("Keine gespeicherten Inhalte für diese Aufgabe gefunden"));

        TaskContent taskContent = studentTaskViewSupportService.getRequestedContent(userTask, version);
        if (taskContent == null) {
            throw new StudentResourceNotFoundException(
                version != null
                    ? "Version " + version + " für diese Aufgabe nicht gefunden"
                    : "Kein gespeicherter Inhalt für diese Aufgabe gefunden"
            );
        }

        model.addAttribute("taskId", taskId);
        model.addAttribute("taskTitle", task.getTitle());
        model.addAttribute("username", currentUser.getName());
        model.addAttribute("content", taskContent.getContent());
        model.addAttribute("version", taskContent.getVersion());
        model.addAttribute("savedAt", taskContent.getSavedAt());
        model.addAttribute("isSubmitted", taskContent.isSubmitted());

        return "debug/content-viewer";
    }
}
