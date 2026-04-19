package com.example.studenttask.controller;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserTaskRepository;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskReviewService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskStatusService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private TaskReviewService taskReviewService;

    @Mock
    private GroupService groupService;

    @Mock
    private TaskContentRepository taskContentRepository;

    @InjectMocks
    private StudentController controller;

    @Test
    void dashboard_returnsRecentTasksAndStatusCounts() {
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
                List.of(userTask1, userTask2, userTask3, userTask4));

        Model model = new ExtendedModelMap();
        String view = controller.dashboard(model, principal("oidc-subject"));

        assertThat(view).isEqualTo("student/dashboard");
        assertThat(model.getAttribute("student")).isSameAs(student);
        assertThat(model.getAttribute("totalTaskCount")).isEqualTo(4);
        assertThat(model.getAttribute("inProgress")).isEqualTo(1L);
        assertThat(model.getAttribute("pendingReview")).isEqualTo(1L);
        assertThat(model.getAttribute("needsRework")).isEqualTo(1L);
        assertThat(model.getAttribute("completed")).isEqualTo(1L);

        @SuppressWarnings("unchecked")
        List<UserTask> recentUserTasks = (List<UserTask>) model.getAttribute("userTasks");
        assertThat(recentUserTasks).containsExactly(userTask1, userTask2, userTask3);
    }

    @Test
    void taskList_groupsTasksByUnitTitleAndSortsUnitsAndTasks() {
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
                List.of(userTaskB, userTaskA, userTaskIntro));

        Model model = new ExtendedModelMap();
        String view = controller.taskList(model, principal("oidc-subject"));

        assertThat(view).isEqualTo("student/tasks-list");

        @SuppressWarnings("unchecked")
        Map<UnitTitle, List<UserTask>> tasksByUnitTitle =
                (Map<UnitTitle, List<UserTask>>) model.getAttribute("tasksByUnitTitle");

        assertThat(new ArrayList<>(tasksByUnitTitle.keySet())).containsExactly(basics, advanced);
        assertThat(tasksByUnitTitle.get(basics)).containsExactly(userTaskIntro);
        assertThat(tasksByUnitTitle.get(advanced)).containsExactly(userTaskA, userTaskB);
        assertThat(model.getAttribute("userTasks")).isEqualTo(List.of(userTaskB, userTaskA, userTaskIntro));
    }

    @Test
    void taskHistory_createsMissingUserTaskWithDefaultStatusFromService() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(301L, "History Task", group, null);
        TaskStatus defaultStatus = status("NICHT_BEGONNEN");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(taskService.findById(301L)).thenReturn(Optional.of(task));
        when(userTaskRepository.findByUserAndTask(student, task)).thenReturn(Optional.empty());
        when(taskStatusService.getDefaultStatus()).thenReturn(defaultStatus);
        when(userTaskRepository.save(any(UserTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskContentService.getAllContentVersions(any(UserTask.class))).thenReturn(List.of());
        when(taskReviewService.findByUserTaskOrderByReviewedAtDesc(any(UserTask.class))).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        String view = controller.taskHistory(301L, model, principal("oidc-subject"));

        assertThat(view).isEqualTo("student/task-history");
        UserTask userTask = (UserTask) model.getAttribute("userTask");
        assertThat(userTask.getStatus()).isSameAs(defaultStatus);
        verify(taskStatusService).getDefaultStatus();
    }

    @Test
    void viewTask_usesTaskViewTemplateWhenPresent() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        TaskView taskView = new TaskView();
        taskView.setId(7L);
        taskView.setTemplatePath("taskviews/task-view");

        Task task = task(401L, "Legacy Task", group, null);
        task.setTaskView(taskView);

        UserTask userTask = userTask(student, task, status("IN_BEARBEITUNG"), LocalDateTime.now());

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(taskService.findById(401L)).thenReturn(Optional.of(task));
        when(userTaskService.findByUserIdAndTaskId(student.getId(), 401L)).thenReturn(Optional.of(userTask));
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String view = controller.viewTask(401L, model, principal("oidc-subject"));

        assertThat(view).isEqualTo("taskviews/task-view");
        assertThat(model.getAttribute("taskView")).isSameAs(taskView);
    }

    private void stubStudentTaskAggregation(User student, Group group, List<Task> tasks, List<UserTask> userTasks) {
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(groupService.findGroupsByUserId(student.getId())).thenReturn(Set.of(group));
        when(taskRepository.findByAssignedGroupsContainingAndIsActiveTrue(group)).thenReturn(tasks);
        when(userTaskRepository.findByUser(student)).thenReturn(userTasks);

        for (int i = 0; i < tasks.size(); i++) {
            when(userTaskRepository.findByUserAndTask(student, tasks.get(i))).thenReturn(Optional.of(userTasks.get(i)));
        }
    }

    private Principal principal(String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
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
