
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private GroupService groupService;

    /**
     * Lehrer Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        // Statistiken für Dashboard
        List<Task> activeTasks = taskService.getActiveTasksByTeacher(teacher);
        int activeTaskCount = activeTasks.size();
        
        // Gruppen mit aktiven Aufgaben
        Set<Group> groupsWithTasks = activeTasks.stream()
            .flatMap(task -> task.getAssignedGroups().stream())
            .collect(Collectors.toSet());
        
        model.addAttribute("teacher", teacher);
        model.addAttribute("activeTaskCount", activeTaskCount);
        model.addAttribute("groupCount", groupsWithTasks.size());
        model.addAttribute("recentTasks", activeTasks.stream().limit(5).collect(Collectors.toList()));
        
        return "teacher/dashboard";
    }

    /**
     * Zeigt alle Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<Task> tasks = taskService.getTasksByTeacher(teacher);
        
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
     * Verarbeitet das Erstellen einer neuen Aufgabe
     */
    @PostMapping("/tasks/create")
    public String createTask(@ModelAttribute Task task,
                           @RequestParam(required = false) Long taskViewId,
                           @RequestParam(required = false) List<Long> selectedGroups,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        task.setCreatedBy(teacher);
        task.setCreatedAt(LocalDateTime.now());
        task.setActive(true);
        
        // TaskView setzen
        if (taskViewId != null) {
            Optional<TaskView> taskViewOpt = taskViewService.findById(taskViewId);
            if (taskViewOpt.isPresent()) {
                task.setTaskView(taskViewOpt.get());
            }
        }
        
        // Basisaufgabe: Wenn keine View ausgewählt, setze "Basisaufgabe"
        if (task.getTaskView() == null) {
            Optional<TaskView> baseTaskViewOpt = taskViewService.findByName("Basisaufgabe");
            if (baseTaskViewOpt.isPresent()) {
                TaskView baseTaskView = baseTaskViewOpt.get();
                task.setTaskView(baseTaskView);
            } else {
                // Fallback: Erste verfügbare TaskView verwenden
                List<TaskView> taskViews = taskViewService.findActiveTaskViews();
                if (!taskViews.isEmpty()) {
                    task.setTaskView(taskViews.get(0));
                }
            }
        }

        // Ausgewählte Gruppen zuweisen - selectedGroups ist bereits List<Long>
        Set<Group> assignedGroups = new HashSet<>();
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            assignedGroups = teacher.getGroups().stream()
                .filter(group -> selectedGroups.contains(group.getId()))
                .collect(Collectors.toSet());
        }
        task.setAssignedGroups(assignedGroups);

        // Aufgabe speichern
        Task savedTask = taskService.createTask(task.getTitle(), task.getDescription(), 
            task.getDefaultSubmission(), teacher, task.getDueDate(), task.getTaskView(), 
            assignedGroups);

        redirectAttributes.addFlashAttribute("success", "Aufgabe '" + savedTask.getTitle() + "' wurde erfolgreich erstellt.");
        return "redirect:/teacher/tasks";
    }

    /**
     * Zeigt Details einer Aufgabe mit allen Submissions
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        
        // Sicherheitscheck: Lehrer darf nur eigene Aufgaben sehen
        if (!task.getCreatedBy().equals(teacher)) {
            throw new RuntimeException("Keine Berechtigung für diese Aufgabe");
        }
        
        // Alle UserTasks für diese Aufgabe laden
        List<UserTask> userTasks = taskService.getUserTasksByTask(task);
        
        model.addAttribute("task", task);
        model.addAttribute("userTasks", userTasks);
        model.addAttribute("teacher", teacher);
        
        return "teacher/task-submissions";
    }

    /**
     * Deaktiviert eine Aufgabe
     */
    @PostMapping("/tasks/{taskId}/deactivate")
    public String deactivateTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttributes) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        
        // Sicherheitscheck
        if (!task.getCreatedBy().equals(teacher)) {
            throw new RuntimeException("Keine Berechtigung für diese Aufgabe");
        }
        
        taskService.deactivateTask(task);
        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde deaktiviert.");
        
        return "redirect:/teacher/tasks";
    }

    /**
     * Aktiviert eine Aufgabe wieder
     */
    @PostMapping("/tasks/{taskId}/activate")
    public String activateTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttributes) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        
        // Sicherheitscheck
        if (!task.getCreatedBy().equals(teacher)) {
            throw new RuntimeException("Keine Berechtigung für diese Aufgabe");
        }
        
        taskService.activateTask(task);
        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde aktiviert.");
        
        return "redirect:/teacher/tasks";
    }
}
