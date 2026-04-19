package com.example.studenttask.service;

import com.example.studenttask.dto.StudentDashboardDataDto;
import com.example.studenttask.dto.StudentTaskListDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskOverviewServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private GroupService groupService;

    @Mock
    private TaskContentRepository taskContentRepository;

    @InjectMocks
    private StudentTaskOverviewService studentTaskOverviewService;

    @Test
    void getDashboardData_returnsRecentTasksAndStatusCounts() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");

        Task task1 = task(101L, "Task 1", group, null);
        Task task2 = task(102L, "Task 2", group, null);
        Task task3 = task(103L, "Task 3", group, null);
        Task task4 = task(104L, "Task 4", group, null);

        UserTask userTask1 = userTask(student, task1, status("IN_BEARBEITUNG"), LocalDateTime.now().minusHours(1));
        UserTask userTask2 = userTask(student, task2, status("ABGEGEBEN"), LocalDateTime.now().minusHours(2));
        UserTask userTask3 = userTask(student, task3, status("\u00dcBERARBEITUNG_N\u00d6TIG"), LocalDateTime.now().minusHours(3));
        UserTask userTask4 = userTask(student, task4, status("VOLLST\u00c4NDIG"), LocalDateTime.now().minusHours(4));

        stubStudentTaskAggregation(student, group,
            List.of(task1, task2, task3, task4),
            List.of(userTask1, userTask2, userTask3, userTask4)
        );

        StudentDashboardDataDto dashboardData = studentTaskOverviewService.getDashboardData(student);

        assertThat(dashboardData.getTotalTaskCount()).isEqualTo(4);
        assertThat(dashboardData.getInProgress()).isEqualTo(1);
        assertThat(dashboardData.getPendingReview()).isEqualTo(1);
        assertThat(dashboardData.getNeedsRework()).isEqualTo(1);
        assertThat(dashboardData.getCompleted()).isEqualTo(1);
        assertThat(dashboardData.getRecentUserTasks()).containsExactly(userTask1, userTask2, userTask3);
    }

    @Test
    void getTaskListData_groupsTasksByUnitTitleAndSortsUnitsAndTasks() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        UnitTitle basics = unitTitle("basics", "Basics", 10);
        UnitTitle advanced = unitTitle("advanced", "Advanced", 20);

        Task taskB = task(201L, "B Task", group, advanced);
        Task taskA = task(202L, "A Task", group, advanced);
        Task taskIntro = task(203L, "Intro", group, basics);

        UserTask userTaskB = userTask(student, taskB, status("IN_BEARBEITUNG"), LocalDateTime.now().minusHours(3));
        UserTask userTaskA = userTask(student, taskA, status("ABGEGEBEN"), LocalDateTime.now().minusHours(2));
        UserTask userTaskIntro = userTask(student, taskIntro, status("VOLLST\u00c4NDIG"), LocalDateTime.now().minusHours(1));

        stubStudentTaskAggregation(student, group,
            List.of(taskB, taskA, taskIntro),
            List.of(userTaskB, userTaskA, userTaskIntro)
        );

        StudentTaskListDataDto taskListData = studentTaskOverviewService.getTaskListData(student);

        Map<UnitTitle, List<UserTask>> tasksByUnitTitle = taskListData.getTasksByUnitTitle();
        assertThat(new ArrayList<>(tasksByUnitTitle.keySet())).containsExactly(basics, advanced);
        assertThat(tasksByUnitTitle.get(basics)).containsExactly(userTaskIntro);
        assertThat(tasksByUnitTitle.get(advanced)).containsExactly(userTaskA, userTaskB);
        assertThat(taskListData.getUserTasks()).isEqualTo(List.of(userTaskB, userTaskA, userTaskIntro));
    }

    @Test
    void getTaskListData_createsMissingUserTaskForRelevantTask() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        Task task = task(301L, "Worksheet", group, null);
        TaskStatus defaultStatus = status("NICHT_BEGONNEN");

        when(groupService.findGroupsByUserId(student.getId())).thenReturn(Set.of(group));
        when(taskRepository.findByAssignedGroupsContainingAndIsActiveTrue(group)).thenReturn(List.of(task));
        when(userTaskRepository.findByUser(student)).thenReturn(List.of());
        when(userTaskRepository.findByUserAndTask(student, task)).thenReturn(Optional.empty());
        when(taskStatusService.getDefaultStatus()).thenReturn(defaultStatus);
        when(userTaskRepository.save(any(UserTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentTaskListDataDto taskListData = studentTaskOverviewService.getTaskListData(student);

        assertThat(taskListData.getUserTasks()).hasSize(1);
        UserTask createdUserTask = taskListData.getUserTasks().get(0);
        assertThat(createdUserTask.getTask()).isSameAs(task);
        assertThat(createdUserTask.getUser()).isSameAs(student);
        assertThat(createdUserTask.getStatus()).isSameAs(defaultStatus);
        assertThat(createdUserTask.getStartedAt()).isNotNull();
        verify(taskStatusService).getDefaultStatus();
        verify(userTaskRepository).save(any(UserTask.class));
    }

    private void stubStudentTaskAggregation(User student, Group group, List<Task> tasks, List<UserTask> userTasks) {
        when(groupService.findGroupsByUserId(student.getId())).thenReturn(Set.of(group));
        when(taskRepository.findByAssignedGroupsContainingAndIsActiveTrue(group)).thenReturn(tasks);
        when(userTaskRepository.findByUser(student)).thenReturn(userTasks);

        for (int i = 0; i < tasks.size(); i++) {
            when(userTaskRepository.findByUserAndTask(student, tasks.get(i))).thenReturn(Optional.of(userTasks.get(i)));
        }
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, Group group, UnitTitle unitTitle) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setIsActive(true);
        task.setAssignedGroups(Set.of(group));
        task.setUnitTitle(unitTitle);
        return task;
    }

    private UnitTitle unitTitle(String id, String name, int weight) {
        return new UnitTitle(id, name, name + " description", weight);
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(User user, Task task, TaskStatus status, LocalDateTime lastModified) {
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setStatus(status);
        userTask.setStartedAt(lastModified.minusMinutes(5));
        userTask.setLastModified(lastModified);
        return userTask;
    }
}
