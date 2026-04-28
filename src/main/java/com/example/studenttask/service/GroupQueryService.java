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
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private UserService userService;

    @Value("${app.teacher.views.include-teachers:false}")
    private boolean includeTeachersInTeacherViews;

    public List<GroupOverviewDto> getGroupsWithActiveTasksByTeacher(User teacher) {
        List<GroupOverviewDto> result = new ArrayList<>();
        List<Task> teacherActiveTasks = taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true);
        List<Group> teacherGroups = new ArrayList<>(teacher.getGroups());

        for (Group group : teacherGroups) {
            List<User> students = getStudentsForGroup(group);
            List<Task> groupActiveTasks = sortTasksForGroup(group, teacherActiveTasks);

            if (!groupActiveTasks.isEmpty()) {
                GroupStatisticsDto statistics = buildGroupStatistics(students, groupActiveTasks);
                int studentCount = students.size();
                int activeTaskCount = groupActiveTasks.size();
                int pendingSubmissions = statistics.getSubmittedTasks();
                LocalDateTime lastActivity = getLastActivityForGroup(students, groupActiveTasks);

                result.add(new GroupOverviewDto(group, studentCount, activeTaskCount, pendingSubmissions, lastActivity));
            }
        }

        return result;
    }

    public StudentTaskMatrixDto getStudentTaskMatrix(Group group, User teacher) {
        List<User> students = getStudentsForGroup(group)
            .stream()
            .sorted((s1, s2) -> {
                String name1 = s1.getFamilyName() != null ? s1.getFamilyName() : s1.getName();
                String name2 = s2.getFamilyName() != null ? s2.getFamilyName() : s2.getName();
                return name1.compareTo(name2);
            })
            .collect(Collectors.toList());

        List<Task> activeTasks = sortTasksForGroup(
            group,
            taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true)
        );
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
        List<User> students = getStudentsForGroup(group);
        List<Task> activeTasks = sortTasksForGroup(
            group,
            taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true)
        );
        return buildGroupStatistics(students, activeTasks);
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

    private LocalDateTime getLastActivityForGroup(List<User> students, List<Task> tasks) {
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

    private List<User> getStudentsForGroup(Group group) {
        return userRepository.findByGroupsContaining(group).stream()
            .filter(this::shouldIncludeGroupMember)
            .collect(Collectors.toList());
    }

    private boolean shouldIncludeGroupMember(User user) {
        return userService.hasStudentRole(user)
            || (includeTeachersInTeacherViews && userService.hasTeacherRole(user));
    }

    private GroupStatisticsDto buildGroupStatistics(List<User> students, List<Task> activeTasks) {
        int submittedTasks = 0;
        int needsRevisionTasks = 0;
        int completedTasks = 0;

        for (Task task : activeTasks) {
            for (User student : students) {
                Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
                if (userTaskOpt.isEmpty()) {
                    continue;
                }

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

        return new GroupStatisticsDto(students.size(), submittedTasks, needsRevisionTasks, completedTasks);
    }
}
