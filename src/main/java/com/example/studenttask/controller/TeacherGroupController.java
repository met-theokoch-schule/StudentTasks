package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.security.Principal;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/teacher/groups")
@PreAuthorize("@userService.hasTeacherRole(authentication.name)")
public class TeacherGroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    /**
     * Zeigt alle Gruppen mit aktiven Aufgaben
     */
    @GetMapping
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Lade Gruppen mit aktiven Aufgaben des Lehrers
        List<GroupInfo> groups = groupService.getGroupsWithActiveTasksByTeacher(teacher);

        model.addAttribute("groups", groups);
        model.addAttribute("teacher", teacher);

        return "teacher/groups-list";
    }

    

        // Lade Matrix-Daten für die Gruppe
        Map<String, Object> matrix = groupService.getStudentTaskMatrix(group, teacher);

        model.addAttribute("group", group);
        model.addAttribute("statistics", statistics);
        model.addAttribute("matrix", matrix);
        model.addAttribute("teacher", teacher);

        return "teacher/group-detail";
    }

    // Helper Classes für Template-Daten

    public static class StudentTaskMatrix {
        private List<User> students;
        private List<Task> tasks;
        private Map<String, UserTaskStatus> statusMap;

        public StudentTaskMatrix() {}

        public StudentTaskMatrix(List<User> students, List<Task> tasks, Map<String, UserTaskStatus> statusMap) {
            this.students = students;
            this.tasks = tasks;
            this.statusMap = statusMap;
        }

        // Getters and Setters
        public List<User> getStudents() { return students; }
        public void setStudents(List<User> students) { this.students = students; }

        public List<Task> getTasks() { return tasks; }
        public void setTasks(List<Task> tasks) { this.tasks = tasks; }

        public Map<String, UserTaskStatus> getStatusMap() { return statusMap; }
        public void setStatusMap(Map<String, UserTaskStatus> statusMap) { this.statusMap = statusMap; }

        public UserTaskStatus getStatus(Long studentId, Long taskId) {
            return statusMap.get(studentId + "_" + taskId);
        }

        public String getStatusIcon(Long studentId, Long taskId) {
            UserTaskStatus status = getStatus(studentId, taskId);
            if (status == null || status.getStatus() == null) {
                return "fas fa-circle text-secondary";
            }

            switch (status.getStatus().getName()) {
                case "NICHT_BEGONNEN": return "fas fa-circle text-secondary";
                case "IN_BEARBEITUNG": return "fas fa-edit text-primary";
                case "ABGEGEBEN": return "fas fa-hourglass-half text-warning";
                case "ÜBERARBEITUNG_NÖTIG": return "fas fa-redo text-danger";
                case "VOLLSTÄNDIG": return "fas fa-check-circle text-success";
                default: return "fas fa-question text-muted";
            }
        }
    }

    public static class UserTaskStatus {
        private TaskStatus status;
        private boolean hasSubmissions;
        private Long userTaskId;

        public UserTaskStatus() {}

        public UserTaskStatus(TaskStatus status, boolean hasSubmissions, Long userTaskId) {
            this.status = status;
            this.hasSubmissions = hasSubmissions;
            this.userTaskId = userTaskId;
        }

        // Getters and Setters
        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus status) { this.status = status; }

        public boolean isHasSubmissions() { return hasSubmissions; }
        public void setHasSubmissions(boolean hasSubmissions) { this.hasSubmissions = hasSubmissions; }

        public Long getUserTaskId() { return userTaskId; }
        public void setUserTaskId(Long userTaskId) { this.userTaskId = userTaskId; }
    }

    public static class GroupInfo {
        private Group group;
        private int studentCount;
        private int activeTaskCount;
        private int pendingSubmissions;
        private LocalDateTime lastActivity;

        // Constructors
        public GroupInfo() {}

        public GroupInfo(Group group, int studentCount, int activeTaskCount, int pendingSubmissions, LocalDateTime lastActivity) {
            this.group = group;
            this.studentCount = studentCount;
            this.activeTaskCount = activeTaskCount;
            this.pendingSubmissions = pendingSubmissions;
            this.lastActivity = lastActivity;
        }

        // Getters and Setters
        public Group getGroup() { return group; }
        public void setGroup(Group group) { this.group = group; }

        public int getStudentCount() { return studentCount; }
        public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

        public int getActiveTaskCount() { return activeTaskCount; }
        public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }

        public int getPendingSubmissions() { return pendingSubmissions; }
        public void setPendingSubmissions(int pendingSubmissions) { this.pendingSubmissions = pendingSubmissions; }

        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    }

    public static class GroupStatistics {
        private int totalStudents;
        private int activeTasks;
        private int pendingSubmissions;
        private int completedSubmissions;

        // Constructors
        public GroupStatistics() {}

        public GroupStatistics(int totalStudents, int activeTasks, int pendingSubmissions, int completedSubmissions) {
            this.totalStudents = totalStudents;
            this.activeTasks = activeTasks;
            this.pendingSubmissions = pendingSubmissions;
            this.completedSubmissions = completedSubmissions;
        }

        // Getters and Setters
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

        public int getActiveTasks() { return activeTasks; }
        public void setActiveTasks(int activeTasks) { this.activeTasks = activeTasks; }

        public int getPendingSubmissions() { return pendingSubmissions; }
        public void setPendingSubmissions(int pendingSubmissions) { this.pendingSubmissions = pendingSubmissions; }

        public int getCompletedSubmissions() { return completedSubmissions; }
        public void setCompletedSubmissions(int completedSubmissions) { this.completedSubmissions = completedSubmissions; }
    }

    public static class StudentTaskInfo {
        private User student;
        private List<TaskInfo> tasks;

        // Constructors
        public StudentTaskInfo() {}

        public StudentTaskInfo(User student, List<TaskInfo> tasks) {
            this.student = student;
            this.tasks = tasks;
        }

        // Getters and Setters
        public User getStudent() { return student; }
        public void setStudent(User student) { this.student = student; }

        public List<TaskInfo> getTasks() { return tasks; }
        public void setTasks(List<TaskInfo> tasks) { this.tasks = tasks; }
    }

    public static class TaskInfo {
        private Long userTaskId;
        private com.example.studenttask.model.Task task;
        private com.example.studenttask.model.TaskStatus status;
        private boolean hasSubmissions;
        private String statusBadgeClass;

        // Constructors
        public TaskInfo() {}

        public TaskInfo(Long userTaskId, com.example.studenttask.model.Task task,
                       com.example.studenttask.model.TaskStatus status, boolean hasSubmissions) {
            this.userTaskId = userTaskId;
            this.task = task;
            this.status = status;
            this.hasSubmissions = hasSubmissions;
            this.statusBadgeClass = determineStatusBadgeClass(status);
        }

        private String determineStatusBadgeClass(com.example.studenttask.model.TaskStatus status) {
            if (status == null) return "bg-secondary";

            switch (status.getName().toUpperCase()) {
                case "NICHT_BEGONNEN":
                    return "bg-secondary";
                case "IN_BEARBEITUNG":
                    return "bg-warning";
                case "ABGEGEBEN":
                    return "bg-info";
                case "ÜBERARBEITUNG_NÖTIG":
                    return "bg-danger";
                case "VOLLSTÄNDIG":
                    return "bg-success";
                default:
                    return "bg-secondary";
            }
        }

        // Getters and Setters
        public Long getUserTaskId() { return userTaskId; }
        public void setUserTaskId(Long userTaskId) { this.userTaskId = userTaskId; }

        public com.example.studenttask.model.Task getTask() { return task; }
        public void setTask(com.example.studenttask.model.Task task) { this.task = task; }

        public com.example.studenttask.model.TaskStatus getStatus() { return status; }
        public void setStatus(com.example.studenttask.model.TaskStatus status) {
            this.status = status;
            this.statusBadgeClass = determineStatusBadgeClass(status);
        }

        public boolean isHasSubmissions() { return hasSubmissions; }
        public void setHasSubmissions(boolean hasSubmissions) { this.hasSubmissions = hasSubmissions; }

        public String getStatusBadgeClass() { return statusBadgeClass; }
        public void setStatusBadgeClass(String statusBadgeClass) { this.statusBadgeClass = statusBadgeClass; }
    }

    @GetMapping("/{groupId}")
    public String groupDetail(@PathVariable Long groupId, Model model, Principal principal, HttpServletRequest request) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        if (teacher == null) {
            return "redirect:/dashboard";
        }

        Group group = groupService.findById(groupId);
        if (group == null) {
            return "redirect:/teacher/groups";
        }

        // Lade Gruppenstatistiken
        GroupStatistics statistics = groupService.getGroupStatistics(group, teacher);

        // Current URL für returnUrl
        String currentUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }

        model.addAttribute("group", group);
        model.addAttribute("statistics", statistics);
        model.addAttribute("currentUrl", currentUrl);

        return "teacher/group-detail";
    }
}