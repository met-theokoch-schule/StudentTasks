package com.example.studenttask.service;

import com.example.studenttask.controller.TeacherGroupController.*;
import com.example.studenttask.model.*;
import com.example.studenttask.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public List<GroupInfo> getGroupsWithActiveTasksByTeacher(User teacher) {
        List<GroupInfo> result = new ArrayList<>();

        // Alle aktiven Aufgaben des Lehrers
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);

        // Gruppiere nach Gruppen
        activeTasks.stream()
            .flatMap(task -> task.getAssignedGroups().stream())
            .distinct()
            .forEach(group -> {
                // Zähle Statistiken für diese Gruppe
                int studentCount = userRepository.countByGroupsContaining(group);
                int activeTaskCount = (int) activeTasks.stream()
                    .filter(task -> task.getAssignedGroups().contains(group))
                    .count();

                // Zähle ausstehende Abgaben
                int pendingSubmissions = countPendingSubmissionsForGroup(group, teacher);

                // Letzte Aktivität
                LocalDateTime lastActivity = getLastActivityForGroup(group, teacher);

                result.add(new GroupInfo(group, studentCount, activeTaskCount, pendingSubmissions, lastActivity));
            });

        return result;
    }

    /**
     * Erstellt Statistiken für eine Gruppe
     */
    public GroupStatistics getGroupStatistics(Group group, User teacher) {
        int totalStudents = userRepository.countByGroupsContaining(group);

        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        int activeTaskCount = activeTasks.size();
        int pendingSubmissions = countPendingSubmissionsForGroup(group, teacher);
        int completedSubmissions = countCompletedSubmissionsForGroup(group, teacher);

        return new GroupStatistics(totalStudents, activeTaskCount, pendingSubmissions, completedSubmissions);
    }

    /**
     * Lädt alle Schüler einer Gruppe mit ihren Aufgaben
     */
    public List<StudentTaskInfo> getStudentTasksForGroup(Group group, User teacher) {
        List<StudentTaskInfo> result = new ArrayList<>();

        // Alle Schüler der Gruppe
        List<User> students = userRepository.findByGroupsContaining(group);

        // Alle aktiven Aufgaben des Lehrers für diese Gruppe
        List<Task> groupTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        for (User student : students) {
            List<TaskInfo> taskInfos = new ArrayList<>();

            for (Task task : groupTasks) {
                // UserTask für diesen Schüler und diese Aufgabe
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();

                    boolean hasSubmissions = taskContentRepository.countByUserTaskAndIsSubmittedTrue(userTask) > 0;

                    TaskInfo taskInfo = new TaskInfo(
                        userTask.getId(),
                        task,
                        userTask.getStatus(),
                        hasSubmissions
                    );
                    taskInfos.add(taskInfo);
                }
            }

            if (!taskInfos.isEmpty()) {
                result.add(new StudentTaskInfo(student, taskInfos));
            }
        }

        return result;
    }

    /**
     * Zählt ausstehende Abgaben für eine Gruppe
     */
    private int countPendingSubmissionsForGroup(Group group, User teacher) {
        List<User> students = userRepository.findByGroupsContaining(group);
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        int count = 0;
        for (User student : students) {
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    if (userTask != null && !isTaskCompleted(userTask)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Zählt abgeschlossene Abgaben für eine Gruppe
     */
    private int countCompletedSubmissionsForGroup(Group group, User teacher) {
        List<User> students = userRepository.findByGroupsContaining(group);
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        int count = 0;
        for (User student : students) {
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    if (userTask != null && isTaskCompleted(userTask)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Findet letzte Aktivität für eine Gruppe
     */
    private LocalDateTime getLastActivityForGroup(Group group, User teacher) {
        List<User> students = userRepository.findByGroupsContaining(group);
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        LocalDateTime latest = null;

        for (User student : students) {
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                 if (userTaskOpt.isPresent()) {
                     UserTask userTask = userTaskOpt.get();
                    if (userTask != null && userTask.getLastModified() != null) {
                        if (latest == null || userTask.getLastModified().isAfter(latest)) {
                            latest = userTask.getLastModified();
                        }
                    }
                }
            }
        }

        return latest;
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
        // Alle Benutzer der Gruppe (ohne Rolle-Filterung)
        List<User> students = userRepository.findByGroupsContaining(group);

        // Alle aktiven Aufgaben des Lehrers für diese Gruppe
        List<Task> tasks = taskRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .collect(Collectors.toList());

        // Status-Matrix aufbauen
        Map<String, Map<String, Object>> statusMap = new HashMap<>();

        for (User student : students) {
            for (Task task : tasks) {
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
        matrix.put("tasks", tasks);
        matrix.put("statusMap", statusMap);

        // Debug-Ausgabe
        System.out.println("Matrix Debug - Group: " + group.getName() + " (ID: " + group.getId() + ")");
        System.out.println("All users in group: " + students.size());
        System.out.println("Tasks found: " + tasks.size());
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

    public TeacherGroupController.GroupStatistics calculateGroupStatistics(Group group, User teacher) {
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
}