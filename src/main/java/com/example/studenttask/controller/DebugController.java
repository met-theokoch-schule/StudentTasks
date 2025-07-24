
package com.example.studenttask.controller;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.service.SubmissionService;
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
        Optional<UserTask> userTaskOpt = userTaskService.findByUserAndTask(currentUser, task);
        if (userTaskOpt.isEmpty()) {
            model.addAttribute("error", "Keine UserTask für Benutzer " + currentUser.getUsername() + " und Task " + taskId + " gefunden");
            return "debug/content-viewer";
        }

        UserTask userTask = userTaskOpt.get();

        // TaskContent basierend auf Version abrufen
        Optional<TaskContent> taskContentOpt;
        if (version != null) {
            // Spezifische Version
            taskContentOpt = taskContentService.findByUserTaskAndVersion(userTask, version);
            if (taskContentOpt.isEmpty()) {
                model.addAttribute("error", "Version " + version + " für Task " + taskId + " nicht gefunden");
                return "debug/content-viewer";
            }
        } else {
            // Neueste Version
            taskContentOpt = taskContentService.findLatestByUserTask(userTask);
            if (taskContentOpt.isEmpty()) {
                model.addAttribute("error", "Keine TaskContent für Task " + taskId + " gefunden");
                return "debug/content-viewer";
            }
        }

        TaskContent taskContent = taskContentOpt.get();

        // Informationen für Template vorbereiten
        model.addAttribute("taskId", taskId);
        model.addAttribute("taskTitle", task.getTitle());
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("content", taskContent.getContent());
        model.addAttribute("version", taskContent.getVersion());
        model.addAttribute("savedAt", taskContent.getSavedAt());
        model.addAttribute("isSubmitted", taskContent.isSubmitted());

        return "debug/content-viewer";
    }
}
