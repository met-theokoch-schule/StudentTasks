
package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
        
        List<Task> recentTasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);
        List<GroupInfo> groupsWithTasks = groupService.getGroupsWithActiveTasksByTeacher(teacher);
        
        model.addAttribute("teacher", teacher);
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("groupsWithTasks", groupsWithTasks);
        
        return "teacher/dashboard";
    }

    /**
     * Zeigt alle Aufgaben des Lehrers
     */
    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<Task> tasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);
        
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
    public String createTask(
            @ModelAttribute Task task,
            @RequestParam("taskViewId") Long taskViewId,
            @RequestParam(value = "selectedGroups", required = false) List<String> selectedGroups,
            @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dueDate,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        try {
            User teacher = userService.findByOpenIdSubject(principal.getName())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

            // TaskView setzen
            TaskView taskView = taskViewService.findById(taskViewId)
                .orElseThrow(() -> new RuntimeException("TaskView nicht gefunden"));
            task.setTaskView(taskView);

            // Due Date setzen
            task.setDueDate(dueDate);

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
     * Zeigt Abgaben für eine bestimmte Aufgabe
     */
    @GetMapping("/tasks/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        
        // Sicherheitsprüfung: Aufgabe gehört dem Lehrer
        if (!task.getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks";
        }
        
        List<UserTask> userTasks = userTaskService.findByTask(task);
        
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
        
        // Sicherheitsprüfung
        if (!task.getCreatedBy().equals(teacher)) {
            redirectAttributes.addFlashAttribute("error", "Sie sind nicht berechtigt, diese Aufgabe zu deaktivieren.");
            return "redirect:/teacher/tasks";
        }
        
        taskService.deactivateTask(taskId);
        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde deaktiviert.");
        
        return "redirect:/teacher/tasks";
    }

    /**
     * Reaktiviert eine Aufgabe
     */
    @PostMapping("/tasks/{taskId}/activate")
    public String activateTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttributes) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        
        // Sicherheitsprüfung
        if (!task.getCreatedBy().equals(teacher)) {
            redirectAttributes.addFlashAttribute("error", "Sie sind nicht berechtigt, diese Aufgabe zu aktivieren.");
            return "redirect:/teacher/tasks";
        }
        
        taskService.activateTask(taskId);
        redirectAttributes.addFlashAttribute("success", "Aufgabe wurde aktiviert.");
        
        return "redirect:/teacher/tasks";
    }

    /**
     * Zeigt alle Gruppen mit aktiven Aufgaben
     */
    @GetMapping("/groups")
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        List<GroupInfo> groupsWithTasks = groupService.getGroupsWithActiveTasksByTeacher(teacher);
        
        model.addAttribute("groupsWithTasks", groupsWithTasks);
        model.addAttribute("teacher", teacher);
        
        return "teacher/groups-list";
    }

    /**
     * Zeigt Details einer Gruppe
     */
    @GetMapping("/groups/{groupId}")
    public String viewGroupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        
        Group group = groupService.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Gruppe nicht gefunden"));
        
        // Sicherheitsprüfung: Lehrer ist der Gruppe zugeordnet
        if (!teacher.getGroups().contains(group)) {
            return "redirect:/teacher/groups";
        }
        
        List<StudentTaskInfo> studentTasks = groupService.getStudentTasksForGroup(group, teacher);
        
        model.addAttribute("group", group);
        model.addAttribute("studentTasks", studentTasks);
        model.addAttribute("teacher", teacher);
        
        return "teacher/group-detail";
    }
}
