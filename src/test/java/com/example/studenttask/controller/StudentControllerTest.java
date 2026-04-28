package com.example.studenttask.controller;

import com.example.studenttask.dto.StudentDashboardDataDto;
import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskListDataDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.StudentTaskOverviewService;
import com.example.studenttask.service.StudentTaskQueryService;
import com.example.studenttask.service.UserService;
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
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private StudentTaskOverviewService studentTaskOverviewService;

    @Mock
    private StudentTaskQueryService studentTaskQueryService;

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

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskOverviewService.getDashboardData(student)).thenReturn(new StudentDashboardDataDto(
            List.of(userTask1, userTask2, userTask3),
            4,
            1,
            1,
            1,
            1
        ));

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

        Map<UnitTitle, List<UserTask>> tasksByUnitTitle = new LinkedHashMap<>();
        tasksByUnitTitle.put(basics, List.of(userTaskIntro));
        tasksByUnitTitle.put(advanced, List.of(userTaskA, userTaskB));

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskOverviewService.getTaskListData(student)).thenReturn(new StudentTaskListDataDto(
            List.of(userTaskB, userTaskA, userTaskIntro),
            tasksByUnitTitle,
            Set.of()
        ));

        Model model = new ExtendedModelMap();
        String view = controller.taskList(model, principal("oidc-subject"));

        assertThat(view).isEqualTo("student/tasks-list");

        @SuppressWarnings("unchecked")
        Map<UnitTitle, List<UserTask>> tasksByUnitTitleFromModel =
                (Map<UnitTitle, List<UserTask>>) model.getAttribute("tasksByUnitTitle");

        assertThat(new ArrayList<>(tasksByUnitTitleFromModel.keySet())).containsExactly(basics, advanced);
        assertThat(tasksByUnitTitleFromModel.get(basics)).containsExactly(userTaskIntro);
        assertThat(tasksByUnitTitleFromModel.get(advanced)).containsExactly(userTaskA, userTaskB);
        assertThat(model.getAttribute("userTasks")).isEqualTo(List.of(userTaskB, userTaskA, userTaskIntro));
        assertThat(model.getAttribute("expandedUnitIds")).isEqualTo(Set.of());
    }

    @Test
    void taskHistory_createsMissingUserTaskWithDefaultStatusFromService() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        Task task = task(301L, "History Task", group, null);
        UserTask userTask = userTask(student, task, status("NICHT_BEGONNEN"), LocalDateTime.now());
        TaskContent contentVersion = new TaskContent();
        contentVersion.setVersion(1);
        TaskReview review = new TaskReview();

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskQueryService.getTaskHistoryData(student, 301L)).thenReturn(
            new StudentTaskHistoryDataDto(task, userTask, List.of(contentVersion), List.of(review))
        );

        Model model = new ExtendedModelMap();
        String view = controller.taskHistory(301L, model, principal("oidc-subject"));

        assertThat(view).isEqualTo("student/task-history");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("contentVersions")).isEqualTo(List.of(contentVersion));
        assertThat(model.getAttribute("reviews")).isEqualTo(List.of(review));
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
        when(studentTaskQueryService.getTaskViewData(student, 401L)).thenReturn(
            new StudentTaskViewDataDto(task, userTask, taskView, "Default content", null, false)
        );

        Model model = new ExtendedModelMap();
        String view = controller.viewTask(401L, model, principal("oidc-subject"));

        assertThat(view).isEqualTo("taskviews/task-view");
        assertThat(model.getAttribute("taskView")).isSameAs(taskView);
        assertThat(model.getAttribute("currentContent")).isEqualTo("Default content");
    }

    @Test
    void viewTaskVersion_usesTaskViewTemplateWhenVersionExists() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        TaskView taskView = new TaskView();
        taskView.setId(8L);
        taskView.setTemplatePath("taskviews/history-view");
        Task task = task(401L, "History Task", group, null);
        task.setTaskView(taskView);
        UserTask userTask = userTask(student, task, status("ABGEGEBEN"), LocalDateTime.now());

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskQueryService.getTaskVersionViewData(student, 401L, 3)).thenReturn(
            new StudentTaskViewDataDto(task, userTask, taskView, "Historic content", 3, true)
        );

        Model model = new ExtendedModelMap();
        String view = controller.viewTaskVersion(401L, 3, model, principal("oidc-subject"));

        assertThat(view).isEqualTo("taskviews/history-view");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("currentContent")).isEqualTo("Historic content");
        assertThat(model.getAttribute("viewingVersion")).isEqualTo(3);
        assertThat(model.getAttribute("isHistoryView")).isEqualTo(true);
    }

    @Test
    void dashboard_throwsAuthenticationExceptionWhenStudentCannotBeResolved() {
        when(userService.findByOpenIdSubject("missing-student")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.dashboard(new ExtendedModelMap(), principal("missing-student")))
            .isInstanceOf(UserAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void taskHistory_throwsNotFoundExceptionWhenTaskIsUnavailable() {
        User student = user(1L, "Student One");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskQueryService.getTaskHistoryData(student, 999L))
            .thenThrow(new StudentResourceNotFoundException("Aufgabe nicht gefunden"));

        assertThatThrownBy(() -> controller.taskHistory(999L, new ExtendedModelMap(), principal("oidc-subject")))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
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
