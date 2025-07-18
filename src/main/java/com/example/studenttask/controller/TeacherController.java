
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private UserTaskService userTaskService;

    /**
     * Lehrer-Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<Task> recentTasks = taskService.findByCreatedBy(teacher);
        
        model.addAttribute("teacher", teacher);
        model.addAttribute("recentTasks", recentTasks);
        
        return "teacher/dashboard";
    }

    /**
     * Zeigt alle Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<Task> tasks = taskService.findByCreatedBy(teacher);
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("teacher", teacher);
        
        return "teacher/tasks-list";
    }

    /**
     * Formular zum Erstellen einer neuen Aufgabe
     */
    @GetMapping("/tasks/create")
    public String createTaskForm(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = new Task();
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();
        List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());
        
        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("teacherGroups", teacherGroups);
        model.addAttribute("teacher", teacher);
        
        return "teacher/task-create";
    }

    /**
     * Verarbeitung der Aufgabenerstellung
     */
    @PostMapping("/tasks/create")
    public String createTask(@Valid @ModelAttribute Task task,
                           BindingResult result,
                           @RequestParam(value = "taskViewId", required = false) Long taskViewId,
                           @RequestParam(value = "selectedGroups", required = false) List<String> selectedGroups,
                           RedirectAttributes redirectAttributes,
                           Principal principal,
                           Model model) {

        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        if (result.hasErrors()) {
            List<TaskView> taskViews = taskViewService.findActiveTaskViews();
            List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());
            
            model.addAttribute("taskViews", taskViews);
            model.addAttribute("teacherGroups", teacherGroups);
            model.addAttribute("teacher", teacher);
            
            return "teacher/task-create";
        }

        try {
            // TaskView setzen falls ausgewählt
            if (taskViewId != null) {
                Optional<TaskView> taskViewOpt = taskViewService.findById(taskViewId);
                if (taskViewOpt.isPresent()) {
                    task.setTaskView(taskViewOpt.get());
                }
            }

            // selectedGroups von String zu Long konvertieren
            List<Long> groupIds = new ArrayList<>();
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                for (String groupIdStr : selectedGroups) {
                    try {
                        groupIds.add(Long.parseLong(groupIdStr));
                    } catch (NumberFormatException e) {
                        // Ignoriere ungültige IDs
                    }
                }
            }

            // Ausgewählte Gruppen zuweisen
            Set<Group> assignedGroups = new HashSet<>();
            if (!groupIds.isEmpty()) {
                assignedGroups = teacher.getGroups().stream()
                    .filter(group -> groupIds.contains(group.getId()))
                    .collect(Collectors.toSet());
            }
            task.setAssignedGroups(assignedGroups);

            // Aufgabe speichern
            Task savedTask = taskService.createTask(task.getTitle(), task.getDescription(), 
                task.getDefaultSubmission(), teacher, task.getDueDate(), task.getTaskView(), 
                assignedGroups);

            redirectAttributes.addFlashAttribute("success", "Aufgabe '" + savedTask.getTitle() + "' wurde erfolgreich erstellt.");
            return "redirect:/teacher/tasks";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Erstellen der Aufgabe: " + e.getMessage());
            return "redirect:/teacher/tasks/create";
        }
    }

    /**
     * Aufgabe bearbeiten
     */
    @GetMapping("/tasks/{taskId}/edit")
    public String editTaskForm(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();
        List<TaskView> taskViews = taskViewService.findActiveTaskViews();
        List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());

        model.addAttribute("task", task);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("teacherGroups", teacherGroups);
        model.addAttribute("teacher", teacher);

        return "teacher/task-edit";
    }

    /**
     * Aufgabe löschen
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, 
                           RedirectAttributes redirectAttributes, 
                           Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            redirectAttributes.addFlashAttribute("error", "Aufgabe nicht gefunden oder keine Berechtigung.");
            return "redirect:/teacher/tasks";
        }

        try {
            taskService.deleteTask(taskId);
            redirectAttributes.addFlashAttribute("success", "Aufgabe wurde erfolgreich gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Löschen der Aufgabe: " + e.getMessage());
        }

        return "redirect:/teacher/tasks";
    }

    /**
     * Aufgaben-Details mit Abgaben
     */
    @GetMapping("/tasks/{taskId}")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty() || !taskOpt.get().getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }

        Task task = taskOpt.get();
        List<UserTask> submissions = userTaskService.findByTask(task);

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);

        return "teacher/task-submissions";
    }

    /**
     * Zeigt alle Gruppen mit aktiven Aufgaben
     */
    @GetMapping("/groups")
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<Group> groups = new ArrayList<>(teacher.getGroups());
        
        model.addAttribute("groups", groups);
        model.addAttribute("teacher", teacher);
        
        return "teacher/groups-list";
    }

    /**
     * Gruppen-Details mit Schülern und ihren Aufgaben
     */
    @GetMapping("/groups/{groupId}")
    public String viewGroupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Optional<Group> groupOpt = groupService.findById(groupId);
        if (groupOpt.isEmpty()) {
            return "redirect:/teacher/groups";
        }

        Group group = groupOpt.get();
        
        // Sicherheitsprüfung: Lehrer muss der Gruppe angehören
        if (!teacher.getGroups().contains(group)) {
            return "redirect:/teacher/groups";
        }

        List<User> students = new ArrayList<>(group.getMembers());
        List<Task> groupTasks = taskService.findByAssignedGroupsContaining(group);

        model.addAttribute("teacher", teacher);
        model.addAttribute("group", group);
        model.addAttribute("students", students);
        model.addAttribute("groupTasks", groupTasks);

        return "teacher/group-detail";
    }
}
