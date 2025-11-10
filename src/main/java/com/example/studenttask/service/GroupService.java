package com.example.studenttask.service;

import com.example.studenttask.model.*;
import com.example.studenttask.repository.*;
import com.example.studenttask.controller.TeacherGroupController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskContentRepository taskContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    /**
     * Findet Gruppe anhand ID
     */
    public Group findById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    /**
     * Findet alle Gruppen mit aktiven Aufgaben eines Lehrers
     */
    public List<TeacherGroupController.GroupInfo> getGroupsWithActiveTasksByTeacher(User teacher) {
        List<TeacherGroupController.GroupInfo> result = new ArrayList<>();

        // Alle aktiven Aufgaben (unabhängig vom Ersteller)
        List<Task> allActiveTasks = taskRepository.findByIsActiveTrueOrderByCreatedAtDesc();

        // Filtere nach Gruppen, bei denen der Lehrer Mitglied ist
        List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());

        // Für jede Gruppe, bei der der Lehrer Mitglied ist
        for (Group group : teacherGroups) {
            // Finde aktive Aufgaben, die dieser Gruppe zugewiesen sind
            List<Task> groupActiveTasks = allActiveTasks.stream()
                .filter(task -> task.getAssignedGroups().contains(group))
                .collect(Collectors.toList());

            if (!groupActiveTasks.isEmpty()) {
                // Zähle Statistiken für diese Gruppe
                int studentCount = userRepository.countByGroupsContaining(group);
                int activeTaskCount = groupActiveTasks.size();

                // Zähle ausstehende Abgaben
                int pendingSubmissions = countPendingSubmissionsForGroup(group, groupActiveTasks);

                // Letzte Aktivität
                LocalDateTime lastActivity = getLastActivityForGroup(group, groupActiveTasks);

                result.add(new TeacherGroupController.GroupInfo(group, studentCount, activeTaskCount, pendingSubmissions, lastActivity));
            }
        }

        return result;
    }



    /**
     * Lädt alle Schüler einer Gruppe mit ihren Aufgaben
     */
    public List<TeacherGroupController.StudentTaskInfo> getStudentTaskInfoForGroup(Group group, User teacher) {
        List<TeacherGroupController.StudentTaskInfo> result = new ArrayList<>();

        // Alle Schüler der Gruppe
        List<User> students = userRepository.findByGroupsContaining(group);

        // Alle aktiven Aufgaben für diese Gruppe (unabhängig vom Ersteller)
        List<Task> groupTasks = taskRepository.findByIsActiveTrueOrderByCreatedAtDesc()
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        for (User student : students) {
            List<TeacherGroupController.TaskInfo> taskInfos = new ArrayList<>();

            for (Task task : groupTasks) {
                // UserTask für diesen Schüler und diese Aufgabe
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();

                    boolean hasSubmissions = taskContentRepository.countByUserTaskAndIsSubmittedTrue(userTask) > 0;

                    TeacherGroupController.TaskInfo taskInfo = new TeacherGroupController.TaskInfo(
                        userTask.getId(),
                        task,
                        userTask.getStatus(),
                        hasSubmissions
                    );
                    taskInfos.add(taskInfo);
                }
            }

            if (!taskInfos.isEmpty()) {
                result.add(new TeacherGroupController.StudentTaskInfo(student, taskInfos));
            }
        }

        return result;
    }

    /**
     * Zählt ausstehende Abgaben für eine Gruppe mit einer gegebenen Liste von Aufgaben
     */
    private int countPendingSubmissionsForGroup(Group group, List<Task> tasks) {
        List<User> students = userRepository.findByGroupsContaining(group);

        int pendingCount = 0;
        for (User student : students) {
            for (Task task : tasks) {
                // Hier muss Optional<UserTask> verwendet werden, da nicht jeder Schüler jede Aufgabe hat
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                // Wenn keine UserTask existiert oder sie nicht gestartet wurde, zählt sie als ausstehend
                if (!userTaskOpt.isPresent() || 
                    (userTaskOpt.get().getStatus() != null && "NICHT_BEGONNEN".equals(userTaskOpt.get().getStatus().getName()))) {
                    pendingCount++;
                }
            }
        }
        return pendingCount;
    }

    /**
     * Zählt abgeschlossene Abgaben für eine Gruppe mit einer gegebenen Liste von Aufgaben
     */
    private int countCompletedSubmissionsForGroup(Group group, List<Task> tasks) {
        List<User> students = userRepository.findByGroupsContaining(group);

        int completedCount = 0;
        for (User student : students) {
            for (Task task : tasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    // Überprüfen, ob der Status abgeschlossen ist
                    if (userTask.getStatus() != null && "VOLLSTÄNDIG".equals(userTask.getStatus().getName())) {
                        completedCount++;
                    }
                }
            }
        }
        return completedCount;
    }

    /**
     * Findet letzte Aktivität für eine Gruppe mit einer gegebenen Liste von Aufgaben
     */
    private LocalDateTime getLastActivityForGroup(Group group, List<Task> tasks) {
        List<User> students = userRepository.findByGroupsContaining(group);

        LocalDateTime lastActivity = null;
        for (User student : students) {
            for (Task task : tasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    if (userTask != null && userTask.getLastModified() != null) {
                        if (lastActivity == null || userTask.getLastModified().isAfter(lastActivity)) {
                            lastActivity = userTask.getLastModified();
                        }
                    }
                }
            }
        }
        return lastActivity;
    }

    /**
     * Prüft ob eine UserTask abgeschlossen ist
     */
    private boolean isTaskCompleted(UserTask userTask) {
        return userTask.getStatus() != null && 
               "VOLLSTÄNDIG".equals(userTask.getStatus().getName());
    }

    /**
     * Findet alle Gruppen
     */
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    /**
     * Find groups by IDs
     */
    public List<Group> findAllById(List<Long> ids) {
        return groupRepository.findAllById(ids);
    }

    public Set<Group> findGroupsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getGroups();
        }
        return new HashSet<>();
    }

    public Map<String, Object> getStudentTaskMatrix(Group group, User teacher) {
        // Alle Schüler der Gruppe laden
        List<User> students = userRepository.findByGroupsContaining(group);

        // Alle aktiven Aufgaben finden, die dieser Gruppe zugewiesen sind (unabhängig vom Ersteller)
        List<Task> activeTasks = taskRepository.findByIsActiveOrderByCreatedAtDesc(true)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        // Status-Matrix aufbauen
        Map<String, Map<String, Object>> statusMap = new HashMap<>();

        for (User student : students) {
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);

                Map<String, Object> statusInfo = new HashMap<>();
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    statusInfo.put("userTaskId", userTask.getId());
                    statusInfo.put("status", userTask.getStatus());
                    statusInfo.put("statusIcon", determineStatusIcon(userTask.getStatus()));
                    statusInfo.put("statusColor", determineStatusColor(userTask.getStatus()));
                    statusInfo.put("hasSubmissions", taskContentRepository.countByUserTaskAndIsSubmittedTrue(userTask) > 0);
                } else {
                    statusInfo.put("status", null);
                    statusInfo.put("hasSubmissions", false);
                    statusInfo.put("userTaskId", null);
                }

                statusMap.put(student.getId() + "_" + task.getId(), statusInfo);
            }
        }

        Map<String, Object> matrix = new HashMap<>();
        matrix.put("students", students);
        matrix.put("tasks", activeTasks);
        matrix.put("statusMap", statusMap);

        // Debug-Ausgabe
        System.out.println("Matrix Debug - Group: " + group.getName() + " (ID: " + group.getId() + ")");
        System.out.println("All users in group: " + students.size());
        System.out.println("Tasks found: " + activeTasks.size());
        System.out.println("StatusMap size: " + statusMap.size());

        // Debug: Liste der Benutzer
        for (User student : students) {
            System.out.println("User: " + student.getName() + " (ID: " + student.getId() + "), Roles: " + 
                student.getRoles().stream().map(role -> role.getName()).collect(Collectors.joining(", ")));
        }

        return matrix;
    }

    private String determineStatusIcon(TaskStatus status) {
        if (status == null) return "fas fa-circle text-secondary";

        switch (status.getName()) {
            case "NICHT_BEGONNEN": return "fas fa-circle text-secondary";
            case "IN_BEARBEITUNG": return "fas fa-edit text-primary";
            case "ABGEGEBEN": return "fas fa-hourglass-half text-warning";
            case "ÜBERARBEITUNG_NÖTIG": return "fas fa-redo text-danger";
            case "VOLLSTÄNDIG": return "fas fa-check-circle text-success";
            default: return "fas fa-question text-muted";
        }
    }

    private String determineStatusColor(TaskStatus status) {
        if (status == null) return "text-secondary";

        switch (status.getName()) {
            case "NICHT_BEGONNEN": return "text-secondary";
            case "IN_BEARBEITUNG": return "text-primary";
            case "ABGEGEBEN": return "text-warning";
            case "ÜBERARBEITUNG_NÖTIG": return "text-danger";
            case "VOLLSTÄNDIG": return "text-success";
            default: return "text-muted";
        }
    }

    public TeacherGroupController.GroupStatistics getGroupStatistics(Group group, User teacher) {
        // Anzahl der Schüler in der Gruppe
        int totalStudents = (int) userRepository.countByGroupsContaining(group);

        // Alle aktiven Aufgaben des Lehrers finden
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true);

        // Statistiken für spezifische Status zählen
        int submittedTasks = 0;
        int needsRevisionTasks = 0;
        int completedTasks = 0;

        for (Task task : activeTasks) {
            if (task.getAssignedGroups().contains(group)) {
                // UserTasks für diese Aufgabe und Gruppe zählen
                List<User> groupUsers = userRepository.findByGroupsContaining(group);
                for (User user : groupUsers) {
                    Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(user, task);
                    if (userTaskOpt.isPresent()) {
                        UserTask userTask = userTaskOpt.get();
                        if (userTask.getStatus() != null) {
                            String statusName = userTask.getStatus().getName();
                            if ("ABGEGEBEN".equals(statusName)) {
                                submittedTasks++;
                            } else if ("ÜBERARBEITUNG_NÖTIG".equals(statusName)) {
                                needsRevisionTasks++;
                            } else if ("VOLLSTÄNDIG".equals(statusName)) {
                                completedTasks++;
                            }
                        }
                    }
                }
            }
        }

        return new TeacherGroupController.GroupStatistics(
            totalStudents, 
            submittedTasks, 
            needsRevisionTasks, 
            completedTasks
        );
    }

    public List<Group> getGroupsForUser(User user) {
        System.out.println("📊 GroupService.getGroupsForUser called for user: " + user.getName() + " (ID: " + user.getId() + ")");
        List<Group> groups = groupRepository.findByUsersContaining(user);
        System.out.println("📊 GroupRepository returned " + (groups != null ? groups.size() : "null") + " groups");
        if (groups != null && !groups.isEmpty()) {
            for (Group group : groups) {
                System.out.println("   📊 Found group: ID=" + group.getId() + ", Name='" + group.getName() + "'");
            }
        }
        return groups;
    }
}