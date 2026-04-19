package com.example.studenttask.service;

import com.example.studenttask.dto.GroupOverviewDto;
import com.example.studenttask.dto.GroupStatisticsDto;
import com.example.studenttask.dto.StudentTaskMatrixDto;
import com.example.studenttask.dto.StudentTaskStatusDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupQueryService {

    private static final Logger log = LoggerFactory.getLogger(GroupQueryService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskContentRepository taskContentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<GroupOverviewDto> getGroupsWithActiveTasksByTeacher(User teacher) {
        List<GroupOverviewDto> result = new ArrayList<>();
        List<Task> allActiveTasks = taskRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());

        for (Group group : teacherGroups) {
            List<Task> groupActiveTasks = sortTasksForGroup(group, allActiveTasks);

            if (!groupActiveTasks.isEmpty()) {
                int studentCount = userRepository.countByGroupsContaining(group);
                int activeTaskCount = groupActiveTasks.size();
                int pendingSubmissions = countPendingSubmissionsForGroup(group, groupActiveTasks);
                LocalDateTime lastActivity = getLastActivityForGroup(group, groupActiveTasks);

                result.add(new GroupOverviewDto(group, studentCount, activeTaskCount, pendingSubmissions, lastActivity));
            }
        }

        return result;
    }

    public StudentTaskMatrixDto getStudentTaskMatrix(Group group) {
        List<User> students = userRepository.findByGroupsContaining(group)
            .stream()
            .sorted((s1, s2) -> {
                String name1 = s1.getFamilyName() != null ? s1.getFamilyName() : s1.getName();
                String name2 = s2.getFamilyName() != null ? s2.getFamilyName() : s2.getName();
                return name1.compareTo(name2);
            })
            .collect(Collectors.toList());

        List<Task> activeTasks = sortTasksForGroup(group, taskRepository.findByIsActiveTrue());
        Map<String, StudentTaskStatusDto> statusMap = new HashMap<>();

        for (User student : students) {
            for (Task task : activeTasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);

                StudentTaskStatusDto statusInfo;
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    statusInfo = new StudentTaskStatusDto(
                        userTask.getStatus(),
                        taskContentRepository.countByUserTaskAndIsSubmittedTrue(userTask) > 0,
                        userTask.getId(),
                        TaskStatusSupport.iconClass(userTask.getStatus()),
                        TaskStatusSupport.textColorClass(userTask.getStatus())
                    );
                } else {
                    statusInfo = new StudentTaskStatusDto(
                        null,
                        false,
                        null,
                        TaskStatusSupport.iconClass(null),
                        TaskStatusSupport.textColorClass(null)
                    );
                }

                statusMap.put(StudentTaskMatrixDto.statusKey(student.getId(), task.getId()), statusInfo);
            }
        }

        log.debug("Built matrix for group {} (id={}) with {} user(s), {} task(s) and {} status entries",
            group.getName(), group.getId(), students.size(), activeTasks.size(), statusMap.size());

        if (log.isDebugEnabled()) {
            for (User student : students) {
                String roles = student.getRoles() == null
                    ? ""
                    : student.getRoles().stream().map(role -> role.getName()).collect(Collectors.joining(", "));
                log.debug("Matrix user {} (id={}) roles={}",
                    student.getName(),
                    student.getId(),
                    roles);
            }
        }

        return new StudentTaskMatrixDto(students, activeTasks, statusMap);
    }

    public GroupStatisticsDto getGroupStatistics(Group group, User teacher) {
        int totalStudents = userRepository.countByGroupsContaining(group);
        List<Task> activeTasks = taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true);
        List<User> groupUsers = userRepository.findByGroupsContaining(group);

        int submittedTasks = 0;
        int needsRevisionTasks = 0;
        int completedTasks = 0;

        for (Task task : activeTasks) {
            if (task.getAssignedGroups().contains(group)) {
                for (User user : groupUsers) {
                    Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(user, task);
                    if (userTaskOpt.isPresent()) {
                        UserTask userTask = userTaskOpt.get();
                        if (TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.ABGEGEBEN)) {
                            submittedTasks++;
                        } else if (TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.UEBERARBEITUNG_NOETIG)) {
                            needsRevisionTasks++;
                        } else if (TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.VOLLSTAENDIG)) {
                            completedTasks++;
                        }
                    }
                }
            }
        }

        return new GroupStatisticsDto(totalStudents, submittedTasks, needsRevisionTasks, completedTasks);
    }

    private List<Task> sortTasksForGroup(Group group, List<Task> tasks) {
        return tasks.stream()
            .filter(task -> task.getAssignedGroups().contains(group))
            .sorted((t1, t2) -> {
                UnitTitle ut1 = t1.getUnitTitle();
                UnitTitle ut2 = t2.getUnitTitle();

                if (ut1 == null && ut2 == null) {
                    return t1.getTitle().compareTo(t2.getTitle());
                }
                if (ut1 == null) {
                    return 1;
                }
                if (ut2 == null) {
                    return -1;
                }

                int weightComparison = Integer.compare(ut1.getWeight(), ut2.getWeight());
                if (weightComparison != 0) {
                    return weightComparison;
                }

                return t1.getTitle().compareTo(t2.getTitle());
            })
            .collect(Collectors.toList());
    }

    private int countPendingSubmissionsForGroup(Group group, List<Task> tasks) {
        List<User> students = userRepository.findByGroupsContaining(group);
        int pendingCount = 0;

        for (User student : students) {
            for (Task task : tasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isEmpty()
                    || TaskStatusSupport.hasCode(userTaskOpt.map(UserTask::getStatus).orElse(null), TaskStatusCode.NICHT_BEGONNEN)) {
                    pendingCount++;
                }
            }
        }

        return pendingCount;
    }

    private LocalDateTime getLastActivityForGroup(Group group, List<Task> tasks) {
        List<User> students = userRepository.findByGroupsContaining(group);
        LocalDateTime lastActivity = null;

        for (User student : students) {
            for (Task task : tasks) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isPresent()) {
                    UserTask userTask = userTaskOpt.get();
                    if (userTask.getLastModified() != null
                        && (lastActivity == null || userTask.getLastModified().isAfter(lastActivity))) {
                        lastActivity = userTask.getLastModified();
                    }
                }
            }
        }

        return lastActivity;
    }
}
