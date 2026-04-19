package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherDashboardQueryService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    public TeacherDashboardDataDto getDashboardData(User teacher) {
        int pendingReviews = countPendingReviews(teacher);
        List<Task> recentTasks = taskService.findByCreatedByOrderByCreatedAtDesc(teacher)
            .stream()
            .limit(5)
            .collect(Collectors.toList());

        return new TeacherDashboardDataDto(pendingReviews, recentTasks);
    }

    public Map<UnitTitle, Map<Task, List<UserTask>>> getGroupedPendingReviews(User teacher) {
        List<Task> teacherTasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);
        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews = new LinkedHashMap<>();

        for (Task task : teacherTasks) {
            List<UserTask> pendingUserTasks = findPendingUserTasksForTask(teacher, task);
            if (!pendingUserTasks.isEmpty()) {
                groupedPendingReviews.computeIfAbsent(task.getUnitTitle(), ignored -> new LinkedHashMap<>())
                    .put(task, pendingUserTasks);
            }
        }

        return groupedPendingReviews;
    }

    public int countPendingReviews(User teacher) {
        return taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .mapToInt(task -> findPendingUserTasksForTask(teacher, task).size())
            .sum();
    }

    private List<UserTask> findPendingUserTasksForTask(User teacher, Task task) {
        Set<Group> teacherGroups = safeGroups(teacher);
        Set<Group> assignedGroups = safeGroups(task.getAssignedGroups());
        List<UserTask> pendingUserTasks = new ArrayList<>();

        for (UserTask userTask : userTaskService.findByTask(task)) {
            if (!TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.ABGEGEBEN)) {
                continue;
            }

            if (sharesAssignedGroup(teacherGroups, safeGroups(userTask.getUser()), assignedGroups)) {
                pendingUserTasks.add(userTask);
            }
        }

        return pendingUserTasks;
    }

    private boolean sharesAssignedGroup(Set<Group> teacherGroups, Set<Group> studentGroups, Set<Group> assignedGroups) {
        for (Group assignedGroup : assignedGroups) {
            if (teacherGroups.contains(assignedGroup) && studentGroups.contains(assignedGroup)) {
                return true;
            }
        }
        return false;
    }

    private Set<Group> safeGroups(User user) {
        return user == null ? Collections.emptySet() : safeGroups(user.getGroups());
    }

    private Set<Group> safeGroups(Set<Group> groups) {
        return groups == null ? Collections.emptySet() : new LinkedHashSet<>(groups);
    }
}
