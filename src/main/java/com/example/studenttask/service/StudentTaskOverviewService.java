package com.example.studenttask.service;

import com.example.studenttask.dto.StudentDashboardDataDto;
import com.example.studenttask.dto.StudentTaskListDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentTaskOverviewService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TaskContentRepository taskContentRepository;

    @Autowired
    private UserService userService;

    public StudentDashboardDataDto getDashboardData(User student) {
        List<UserTask> allUserTasks = getOrCreateUserTasksForStudent(student);
        List<UserTask> recentUserTasks = allUserTasks.stream()
            .filter(this::hasActivityTimestamp)
            .sorted(this::compareByLatestActivityDesc)
            .limit(3)
            .collect(Collectors.toList());

        Map<TaskStatusCode, Long> statusCounts = TaskStatusSupport.countByCode(allUserTasks);

        return new StudentDashboardDataDto(
            recentUserTasks,
            allUserTasks.size(),
            statusCounts.getOrDefault(TaskStatusCode.IN_BEARBEITUNG, 0L),
            statusCounts.getOrDefault(TaskStatusCode.ABGEGEBEN, 0L),
            statusCounts.getOrDefault(TaskStatusCode.UEBERARBEITUNG_NOETIG, 0L),
            statusCounts.getOrDefault(TaskStatusCode.VOLLSTAENDIG, 0L)
        );
    }

    public StudentTaskListDataDto getTaskListData(User student) {
        List<UserTask> userTasks = getOrCreateUserTasksForStudent(student);
        return new StudentTaskListDataDto(
            userTasks,
            groupTasksByUnitTitle(userTasks),
            userService.getStudentTaskListExpandedUnits(student)
        );
    }

    private List<UserTask> getOrCreateUserTasksForStudent(User student) {
        Set<Group> userGroupsSet = groupService.findGroupsByUserId(student.getId());
        List<Group> userGroups = new ArrayList<>(userGroupsSet);

        Set<Long> taskIds = new HashSet<>();
        List<Task> relevantTasks = new ArrayList<>();

        for (Group group : userGroups) {
            List<Task> groupTasks = taskRepository.findByAssignedGroupsContainingAndIsActiveTrue(group);
            for (Task task : groupTasks) {
                if (taskIds.add(task.getId())) {
                    relevantTasks.add(task);
                }
            }
        }

        List<UserTask> existingUserTasks = userTaskRepository.findByUser(student);
        for (UserTask existingUserTask : existingUserTasks) {
            Task task = existingUserTask.getTask();
            if (task.getIsActive() && !taskIds.contains(task.getId())
                    && shouldIncludeTaskOutsideCurrentGroups(task, existingUserTask, userGroups)) {
                taskIds.add(task.getId());
                relevantTasks.add(task);
            }
        }

        List<UserTask> result = new ArrayList<>();
        for (Task task : relevantTasks) {
            Optional<UserTask> existingUserTask = userTaskRepository.findByUserAndTask(student, task);
            if (existingUserTask.isPresent()) {
                result.add(existingUserTask.get());
            } else {
                result.add(createUserTask(student, task));
            }
        }

        return result;
    }

    private Map<UnitTitle, List<UserTask>> groupTasksByUnitTitle(List<UserTask> userTasks) {
        Map<UnitTitle, List<UserTask>> tasksByUnitTitle = new LinkedHashMap<>();

        List<UnitTitle> sortedUnitTitles = userTasks.stream()
            .map(userTask -> userTask.getTask().getUnitTitle())
            .distinct()
            .sorted(unitTitleComparator())
            .collect(Collectors.toList());

        for (UnitTitle unitTitle : sortedUnitTitles) {
            List<UserTask> tasksForUnit = userTasks.stream()
                .filter(userTask -> Objects.equals(userTask.getTask().getUnitTitle(), unitTitle))
                .sorted(Comparator.comparing(userTask -> userTask.getTask().getTitle()))
                .collect(Collectors.toList());

            if (!tasksForUnit.isEmpty()) {
                tasksByUnitTitle.put(unitTitle, tasksForUnit);
            }
        }

        return tasksByUnitTitle;
    }

    private boolean shouldIncludeTaskOutsideCurrentGroups(Task task, UserTask existingUserTask, List<Group> userGroups) {
        if (sharesAssignedGroup(task, userGroups)) {
            return true;
        }

        if (existingUserTask.getStatus() != null
                && !TaskStatusSupport.hasCode(existingUserTask.getStatus(), TaskStatusCode.NICHT_BEGONNEN)) {
            return true;
        }

        return taskContentRepository.countByUserTaskAndIsSubmittedTrue(existingUserTask) > 0;
    }

    private boolean sharesAssignedGroup(Task task, List<Group> userGroups) {
        for (Group assignedGroup : task.getAssignedGroups()) {
            if (userGroups.contains(assignedGroup)) {
                return true;
            }
        }
        return false;
    }

    private UserTask createUserTask(User student, Task task) {
        UserTask userTask = new UserTask();
        userTask.setUser(student);
        userTask.setTask(task);
        userTask.setStatus(taskStatusService.getDefaultStatus());
        userTask.setStartedAt(LocalDateTime.now());
        return userTaskRepository.save(userTask);
    }

    private boolean hasActivityTimestamp(UserTask userTask) {
        return userTask.getLastModified() != null || userTask.getStartedAt() != null;
    }

    private int compareByLatestActivityDesc(UserTask left, UserTask right) {
        LocalDateTime leftTime = latestActivity(left);
        LocalDateTime rightTime = latestActivity(right);
        if (leftTime == null && rightTime == null) {
            return 0;
        }
        if (leftTime == null) {
            return 1;
        }
        if (rightTime == null) {
            return -1;
        }
        return rightTime.compareTo(leftTime);
    }

    private LocalDateTime latestActivity(UserTask userTask) {
        return userTask.getLastModified() != null ? userTask.getLastModified() : userTask.getStartedAt();
    }

    private Comparator<UnitTitle> unitTitleComparator() {
        return (left, right) -> {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }

            int weightComparison = Integer.compare(left.getWeight(), right.getWeight());
            if (weightComparison != 0) {
                return weightComparison;
            }
            return left.getName().compareTo(right.getName());
        };
    }
}
