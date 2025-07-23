package com.example.studenttask.controller;

import com.example.studenttask.dto.GroupStatistics;
import com.example.studenttask.dto.StudentTaskData;
import com.example.studenttask.dto.TaskInfo;
import com.example.studenttask.model.*;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.studenttask.repository.GroupRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher/groups")
@PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
public class TeacherGroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final TaskService taskService;
    private final UserTaskService userTaskService;
    private final com.example.studenttask.repository.TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public TeacherGroupController(GroupService groupService, UserService userService, TaskService taskService, UserTaskService userTaskService,
                                  com.example.studenttask.repository.TaskRepository taskRepository, GroupRepository groupRepository) {
        this.groupService = groupService;
        this.userService = userService;
        this.taskService = taskService;
        this.userTaskService = userTaskService;
        this.taskRepository = taskRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Zeigt alle Gruppen mit aktiven Aufgaben
     */
    @GetMapping
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Lade Gruppen mit aktiven Aufgaben des Lehrers
        List<com.example.studenttask.service.GroupService.GroupInfo> groups = groupService.getGroupsWithActiveTasksByTeacher(teacher);

        model.addAttribute("groups", groups);
        model.addAttribute("teacher", teacher);

        return "teacher/groups-list";
    }

    /**
     * Zeigt die Details einer spezifischen Gruppe mit allen Schülern und ihren Aufgaben-Status
     */
    @GetMapping("/{groupId}")
    public String showGroupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Gruppe nicht gefunden"));

        // Statistiken für die Gruppe berechnen
        GroupStatistics statistics = groupService.getGroupStatistics(group, teacher);
        model.addAttribute("statistics", statistics);

        // Alle Schüler der Gruppe
        List<User> students = userService.findByGroupsContaining(group);

        // Alle aktiven Aufgaben des Lehrers für diese Gruppe
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        // Aufgaben-Informationen erstellen
        List<TaskInfo> tasks = activeTasks.stream()
            .map(task -> {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskId(task.getId());
                taskInfo.setTaskTitle(task.getTitle());
                return taskInfo;
            })
            .collect(Collectors.toList());

        // Schüler-Aufgaben-Daten erstellen
        List<StudentTaskData> studentTasks = new ArrayList<>();
        for (User student : students) {
            StudentTaskData studentData = new StudentTaskData();
            studentData.setStudentId(student.getId());
            studentData.setStudentName(student.getName());

            Map<Long, String> taskStatuses = new HashMap<>();
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskService.findByUserAndTask(student, task);
                String statusName = userTaskOpt.map(ut -> ut.getStatus() != null ? ut.getStatus().getName() : "NICHT_BEGONNEN")
                    .orElse("NICHT_BEGONNEN");
                taskStatuses.put(task.getId(), statusName);
            }
            studentData.setTaskStatuses(taskStatuses);
            studentTasks.add(studentData);
        }

        model.addAttribute("group", group);
        model.addAttribute("tasks", tasks);
        model.addAttribute("studentTasks", studentTasks);

        return "teacher/group-detail";
    }

    public static class GroupInfo {
        private Group group;
        private int activeTaskCount;
        private int totalStudents;
        private int pendingSubmissions;
        private int completedSubmissions;

        // Konstruktor
        public GroupInfo(Group group, int activeTaskCount, int totalStudents, int pendingSubmissions, int completedSubmissions) {
            this.group = group;
            this.activeTaskCount = activeTaskCount;
            this.totalStudents = totalStudents;
            this.pendingSubmissions = pendingSubmissions;
            this.completedSubmissions = completedSubmissions;
        }

        // Getter und Setter
        public Group getGroup() { return group; }
        public void setGroup(Group group) { this.group = group; }

        public int getActiveTaskCount() { return activeTaskCount; }
        public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }

        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

        public int getPendingSubmissions() { return pendingSubmissions; }
        public void setPendingSubmissions(int pendingSubmissions) { this.pendingSubmissions = pendingSubmissions; }

        public int getCompletedSubmissions() { return completedSubmissions; }
        public void setCompletedSubmissions(int completedSubmissions) { this.completedSubmissions = completedSubmissions; }
    }
}