package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.studenttask.repository.*;
import com.example.studenttask.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Schüler-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle aktiven Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Nur aktive Aufgaben anzeigen
        List<UserTask> activeTasks = userTasks.stream()
            .filter(ut -> ut.getTask().getIsActive())
            .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("userTasks", activeTasks);

        return "student/dashboard";
    }

    private List<UserTask> getOrCreateUserTasksForStudent(User student) {
        System.out.println("🔍 === DEBUG: Getting UserTasks for student: " + student.getName() + " (ID: " + student.getId() + ") ===");

        List<UserTask> allUserTasks = new ArrayList<>();

        // Hole alle Gruppen des Benutzers
        Set<Group> userGroups = student.getGroups();
        System.out.println("   - Student is member of " + userGroups.size() + " groups");

        for (Group group : userGroups) {
            System.out.println("   - Checking group: " + group.getName() + " (ID: " + group.getId() + ")");

            // Finde alle aktiven Aufgaben, die dieser Gruppe zugewiesen sind
            List<Task> groupTasks = taskRepository.findByIsActiveTrueAndAssignedGroupsContainsOrderByCreatedAtDesc(group)
                .stream()
                .filter(task -> task.getAssignedGroups().contains(group))
                .collect(Collectors.toList());

            System.out.println("     - Found " + groupTasks.size() + " tasks assigned to this group");

            for (Task task : groupTasks) {
                System.out.println("     - Processing task: " + task.getTitle() + " (ID: " + task.getId() + ")");

                // Prüfe, ob bereits ein UserTask existiert
                Optional<UserTask> existingUserTask = userTaskRepository.findByUserAndTask(student, task);

                if (existingUserTask.isPresent()) {
                    System.out.println("       - UserTask already exists (ID: " + existingUserTask.get().getId() + ")");
                    allUserTasks.add(existingUserTask.get());
                } else {
                    System.out.println("       - Creating new UserTask");
                    // Erstelle einen neuen UserTask
                    UserTask newUserTask = new UserTask();
                    newUserTask.setUser(student);
                    newUserTask.setTask(task);
                    newUserTask.setStartedAt(LocalDateTime.now());
                    // Status auf "NOT_STARTED" setzen (ID: 1)
                    TaskStatus notStartedStatus = taskStatusRepository.findById(1L).orElse(null);
                    newUserTask.setStatus(notStartedStatus);

                    userTaskRepository.save(newUserTask);
                    System.out.println("       - UserTask created with ID: " + newUserTask.getId());
                    allUserTasks.add(newUserTask);
                }
            }
        }

        System.out.println("🔍 === DEBUG: Total UserTasks found/created: " + allUserTasks.size() + " ===");
        return allUserTasks;
    }


    /**
     * Aufgabe bearbeiten
     */
    @GetMapping("/tasks/{taskId}")
    public String editTask(@PathVariable Long taskId, Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

        // UserTask finden oder erstellen
        UserTask userTask = userTaskService.findOrCreateUserTask(student, task);

        // Neuesten Content laden
        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);

        // TaskView laden
        TaskView taskView = taskViewService.findById(task.getTaskView().getId())
            .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));

        model.addAttribute("task", task);
        model.addAttribute("userTask", userTask);
        model.addAttribute("taskView", taskView);
        model.addAttribute("student", student);

        if (latestContent.isPresent()) {
            model.addAttribute("currentContent", latestContent.get().getContent());
        } else {
            model.addAttribute("currentContent", task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "");
        }

        return "student/task-edit";
    }

    /**
     * Aufgabenliste für Schüler
     */
    @GetMapping("/tasks")
    public String taskList(Model model, Principal principal) {
        User student = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Alle aktiven Aufgaben finden, die einer Gruppe des Benutzers zugewiesen sind
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);

        // Nur aktive Aufgaben anzeigen
        List<UserTask> activeTasks = userTasks.stream()
            .filter(ut -> ut.getTask().getIsActive())
            .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("userTasks", activeTasks);

        return "student/tasks-list";
    }
}