
package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.repository.UserRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserTaskRepository;
import com.example.studenttask.repository.TaskContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Debug Controller für interne Entwicklungstools
 * WARNUNG: Dieser Controller ist nur für Debug-Zwecke gedacht!
 */
@Controller
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskContentRepository taskContentRepository;

    /**
     * Zeigt den gespeicherten Content für eine UserTask an
     * URL: /debug/content/{userId}/{taskId}
     * URL mit Version: /debug/content/{userId}/{taskId}?version={versionNumber}
     * 
     * @param userId ID des Users
     * @param taskId ID der Task
     * @param version Optional: spezifische Version (default: neueste)
     * @param model Thymeleaf Model
     * @return Template mit Content-Anzeige
     */
    @GetMapping("/content/{userId}/{taskId}")
    public String showTaskContent(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @RequestParam(required = false) Integer version,
            Model model) {
        
        try {
            // User finden
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                model.addAttribute("error", "User mit ID " + userId + " nicht gefunden");
                return "debug/content-viewer";
            }
            User user = userOpt.get();

            // Task finden
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                model.addAttribute("error", "Task mit ID " + taskId + " nicht gefunden");
                return "debug/content-viewer";
            }
            Task task = taskOpt.get();

            // UserTask finden
            Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(user, task);
            if (!userTaskOpt.isPresent()) {
                model.addAttribute("error", "UserTask für User " + userId + " und Task " + taskId + " nicht gefunden");
                return "debug/content-viewer";
            }
            UserTask userTask = userTaskOpt.get();

            // Content laden
            TaskContent content = null;
            List<TaskContent> allContents = taskContentRepository.findByUserTaskOrderByVersionDesc(userTask);
            
            if (version != null) {
                // Spezifische Version suchen
                content = allContents.stream()
                    .filter(c -> c.getVersion().equals(version))
                    .findFirst()
                    .orElse(null);
                
                if (content == null) {
                    model.addAttribute("error", "Version " + version + " nicht gefunden");
                    return "debug/content-viewer";
                }
            } else {
                // Neueste Version nehmen
                if (!allContents.isEmpty()) {
                    content = allContents.get(0);
                }
            }

            // Model befüllen
            model.addAttribute("user", user);
            model.addAttribute("task", task);
            model.addAttribute("userTask", userTask);
            model.addAttribute("content", content);
            model.addAttribute("allVersions", allContents);
            model.addAttribute("requestedVersion", version);

        } catch (Exception e) {
            model.addAttribute("error", "Fehler beim Laden: " + e.getMessage());
        }

        return "debug/content-viewer";
    }

    /**
     * API Endpoint für Raw Content
     * URL: /debug/api/content/{userId}/{taskId}
     * URL mit Version: /debug/api/content/{userId}/{taskId}?version={versionNumber}
     * 
     * @param userId ID des Users
     * @param taskId ID der Task
     * @param version Optional: spezifische Version
     * @return Raw Content als Plain Text
     */
    @GetMapping("/api/content/{userId}/{taskId}")
    @ResponseBody
    public ResponseEntity<String> getRawContent(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @RequestParam(required = false) Integer version) {
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(userOpt.get(), taskOpt.get());
            if (!userTaskOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            List<TaskContent> allContents = taskContentRepository.findByUserTaskOrderByVersionDesc(userTaskOpt.get());
            
            TaskContent content = null;
            if (version != null) {
                content = allContents.stream()
                    .filter(c -> c.getVersion().equals(version))
                    .findFirst()
                    .orElse(null);
            } else if (!allContents.isEmpty()) {
                content = allContents.get(0);
            }

            if (content == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=utf-8")
                .body(content.getContent());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler: " + e.getMessage());
        }
    }
}
