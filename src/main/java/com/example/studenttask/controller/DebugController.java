package com.example.studenttask.controller;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.service.SubmissionService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Debug Controller - nur für Entwicklungszwecke
 * Ermöglicht das Anzeigen von Aufgabeninhalten für den aktuell eingeloggten Benutzer
 */
@Controller
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

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

        // Aktuellen Benutzer abrufen
        Optional<User> currentUserOpt = authenticationService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            model.addAttribute("error", "Kein Benutzer eingeloggt");
            return "debug/content-viewer";
        }

        User currentUser = currentUserOpt.get();

        // Überprüfung ob Task existiert
        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            model.addAttribute("error", "Aufgabe mit ID " + taskId + " nicht gefunden");
            return "debug/content-viewer";
        }

        Task task = taskOpt.get();

        // Submission suchen
        Submission submission;
        if (version != null) {
            // Spezifische Version
            Optional<Submission> submissionOpt = submissionService.findByUserAndTaskAndVersion(currentUser, task, version);
            if (submissionOpt.isEmpty()) {
                model.addAttribute("error", 
                    "Keine Submission für Aufgabe '" + task.getTitle() + "' in Version " + version + " gefunden");
                return "debug/content-viewer";
            }
            submission = submissionOpt.get();
        } else {
            // Neueste Version
            Optional<Submission> submissionOpt = submissionService.findLatestByUserAndTask(currentUser, task);
            if (submissionOpt.isEmpty()) {
                model.addAttribute("error", 
                    "Keine Submission für Aufgabe '" + task.getTitle() + "' gefunden");
                return "debug/content-viewer";
            }
            submission = submissionOpt.get();
        }

        // Daten für Template
        model.addAttribute("submission", submission);
        model.addAttribute("user", currentUser);
        model.addAttribute("task", task);
        model.addAttribute("content", submission.getContent());
        model.addAttribute("version", submission.getVersion());
        model.addAttribute("submittedAt", submission.getSubmittedAt());

        return "debug/content-viewer";
    }
}