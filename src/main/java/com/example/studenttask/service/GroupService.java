package com.example.studenttask.service;

import com.example.studenttask.controller.TeacherGroupController.*;
import com.example.studenttask.model.*;
import com.example.studenttask.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
}