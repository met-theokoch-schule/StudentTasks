package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.List;

/**
 * Debug Controller - nur für Entwicklungszwecke
 * Ermöglicht das Anzeigen von Aufgabeninhalten für den aktuell eingeloggten Benutzer
 * Komplett unabhängig vom Rest des Systems
 */
@Controller
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

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

        try {
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

            // UserTask für aktuellen Benutzer und Task finden
            List<UserTask> userTasks = userTaskService.findByUser(currentUser);
            Optional<UserTask> userTaskOpt = userTasks.stream()
                .filter(ut -> ut.getTask().getId().equals(taskId))
                .findFirst();

            if (userTaskOpt.isEmpty()) {
                model.addAttribute("error", "Keine UserTask für Benutzer " + currentUser.getName() + " und Task " + taskId + " gefunden");
                return "debug/content-viewer";
            }

            UserTask userTask = userTaskOpt.get();

            // TaskContent basierend auf Version abrufen
            TaskContent taskContent = null;

            if (version != null) {
                // Spezifische Version suchen
                taskContent = taskContentService.getContentByVersion(userTask, version);
                if (taskContent == null) {
                    model.addAttribute("error", "Version " + version + " für Task " + taskId + " nicht gefunden");
                    return "debug/content-viewer";
                }
            } else {
                // Neueste Version
                Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
                if (latestContentOpt.isEmpty()) {
                    model.addAttribute("error", "Keine TaskContent für Task " + taskId + " gefunden");
                    return "debug/content-viewer";
                }
                taskContent = latestContentOpt.get();
            }

            // Informationen für Template vorbereiten
            model.addAttribute("taskId", taskId);
            model.addAttribute("taskTitle", task.getTitle());
            model.addAttribute("username", currentUser.getName());
            model.addAttribute("content", taskContent.getContent());
            model.addAttribute("version", taskContent.getVersion());
            model.addAttribute("savedAt", taskContent.getSavedAt());
            model.addAttribute("isSubmitted", taskContent.isSubmitted());

            return "debug/content-viewer";

        } catch (Exception e) {
            model.addAttribute("error", "Fehler beim Laden der Daten: " + e.getMessage());
            return "debug/content-viewer";
        }
    }
}