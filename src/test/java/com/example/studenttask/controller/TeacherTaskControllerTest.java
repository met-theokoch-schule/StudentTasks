package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.dto.VersionWithSubmissionStatus;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.TeacherTaskQueryService;
import com.example.studenttask.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherTaskControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TeacherTaskQueryService teacherTaskQueryService;

    @Mock
    private TeacherTaskCommandService teacherTaskCommandService;

    @InjectMocks
    private TeacherTaskController controller;

    @Test
    void listTasks_populatesModelFromQueryService() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        UnitTitle noUnitTitle = new UnitTitle();
        noUnitTitle.setName("Aufgaben ohne Thema");

        Map<UnitTitle, List<Task>> tasksByUnitTitle = new LinkedHashMap<>();
        tasksByUnitTitle.put(noUnitTitle, List.of(task));

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(teacherTaskQueryService.getTaskListData(teacher, "own"))
            .thenReturn(new TeacherTaskListDataDto(List.of(task), tasksByUnitTitle));

        Model model = new ExtendedModelMap();
        String view = controller.listTasks("own", model, principal("oidc-teacher"));

        assertThat(view).isEqualTo("teacher/tasks-list");
        assertThat(model.getAttribute("teacher")).isSameAs(teacher);
        assertThat(model.getAttribute("tasks")).isEqualTo(List.of(task));
        assertThat(model.getAttribute("tasksByUnitTitle")).isEqualTo(tasksByUnitTitle);
        assertThat(model.getAttribute("currentFilter")).isEqualTo("own");
    }

    @Test
    void viewTaskSubmissions_populatesModelAndCurrentUrl() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        UserTask userTask = userTask(student(2L, "Student", group), task(20L, "Worksheet", teacher, group, null), status("ABGEGEBEN"));
        TeacherTaskSubmissionsDataDto submissionsData = new TeacherTaskSubmissionsDataDto(
            userTask.getTask(),
            List.of(userTask),
            true
        );

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(teacherTaskQueryService.getTaskSubmissionsData(20L, teacher)).thenReturn(Optional.of(submissionsData));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/teacher/tasks/20/submissions"));
        when(request.getQueryString()).thenReturn("filter=open");

        Model model = new ExtendedModelMap();
        String view = controller.viewTaskSubmissions(20L, model, principal("oidc-teacher"), request);

        assertThat(view).isEqualTo("teacher/task-submissions");
        assertThat(model.getAttribute("task")).isSameAs(userTask.getTask());
        assertThat(model.getAttribute("userTasks")).isEqualTo(List.of(userTask));
        assertThat(model.getAttribute("currentUrl")).isEqualTo("http://localhost/teacher/tasks/20/submissions?filter=open");
        assertThat(model.getAttribute("isOwnTask")).isEqualTo(true);
    }

    @Test
    void reviewSubmission_usesQueryServiceAndRefererAsReturnUrlFallback() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        UserTask userTask = userTask(student(2L, "Student", group), task(20L, "Worksheet", teacher, group, null), status("ABGEGEBEN"));
        TaskReview review = new TaskReview();
        TaskStatus complete = status("VOLLSTAENDIG");
        VersionWithSubmissionStatus versionStatus = new VersionWithSubmissionStatus(2, true, "v2 19.04.26 09:00 👁");

        when(teacherTaskQueryService.getSubmissionReviewData(30L)).thenReturn(Optional.of(
            new TeacherSubmissionReviewDataDto(userTask, List.of(review), List.of(complete), List.of(versionStatus))
        ));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Referer")).thenReturn("http://localhost/teacher/tasks/20/submissions");

        Model model = new ExtendedModelMap();
        String view = controller.reviewSubmission(30L, null, model, request);

        assertThat(view).isEqualTo("teacher/submission-review");
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("reviews")).isEqualTo(List.of(review));
        assertThat(model.getAttribute("statuses")).isEqualTo(List.of(complete));
        assertThat(model.getAttribute("versionsWithStatus")).isEqualTo(List.of(versionStatus));
        assertThat(model.getAttribute("returnUrl")).isEqualTo("http://localhost/teacher/tasks/20/submissions");
    }

    @Test
    void viewSubmissionInTaskView_returnsTemplateFromQueryService() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        UserTask userTask = userTask(student(2L, "Student", group), task(20L, "Worksheet", teacher, group, null), status("ABGEGEBEN"));
        TeacherSubmissionContentViewDto contentView = new TeacherSubmissionContentViewDto(
            userTask.getTask(),
            userTask,
            "Rendered Content",
            3,
            "taskviews/simple-text.html"
        );

        when(teacherTaskQueryService.getSubmissionContentViewData(30L, 3)).thenReturn(Optional.of(contentView));

        Model model = new ExtendedModelMap();
        String view = controller.viewSubmissionInTaskView(30L, 3, model);

        assertThat(view).isEqualTo("taskviews/simple-text.html");
        assertThat(model.getAttribute("task")).isSameAs(userTask.getTask());
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("userTaskId")).isEqualTo(userTask.getId());
        assertThat(model.getAttribute("currentContent")).isEqualTo("Rendered Content");
        assertThat(model.getAttribute("version")).isEqualTo(3);
        assertThat(model.getAttribute("isTeacherView")).isEqualTo(true);
    }

    @Test
    void showCreateTaskForm_populatesModelFromQueryService() {
        Group group = group(10L, "10A");
        TaskView taskView = new TaskView();
        taskView.setId(5L);
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "desc", 10);
        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(new Task(), List.of(taskView), List.of(group), List.of(unitTitle));

        when(teacherTaskQueryService.getCreateTaskFormData()).thenReturn(formData);

        Model model = new ExtendedModelMap();
        String view = controller.showCreateTaskForm(model);

        assertThat(view).isEqualTo("teacher/task-create");
        assertThat(model.getAttribute("task")).isSameAs(formData.getTask());
        assertThat(model.getAttribute("taskViews")).isEqualTo(List.of(taskView));
        assertThat(model.getAttribute("groups")).isEqualTo(List.of(group));
        assertThat(model.getAttribute("unitTitles")).isEqualTo(List.of(unitTitle));
    }

    @Test
    void showEditTaskForm_populatesModelFromQueryService() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        TaskView taskView = new TaskView();
        taskView.setId(5L);
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "desc", 10);
        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(task, List.of(taskView), List.of(group), List.of(unitTitle));

        when(teacherTaskQueryService.getEditTaskFormData(20L)).thenReturn(Optional.of(formData));

        Model model = new ExtendedModelMap();
        String view = controller.showEditTaskForm(20L, model);

        assertThat(view).isEqualTo("teacher/task-edit");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("taskViews")).isEqualTo(List.of(taskView));
        assertThat(model.getAttribute("groups")).isEqualTo(List.of(group));
        assertThat(model.getAttribute("unitTitles")).isEqualTo(List.of(unitTitle));
    }

    @Test
    void submitReview_delegatesToCommandServiceAndRedirectsToReturnUrl() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("currentVersion")).thenReturn("2");
        when(teacherTaskCommandService.submitReview(30L, "oidc-teacher", 7L, "Gut gemacht", "2"))
            .thenReturn(true);

        String view = controller.submitReview(
            30L,
            7L,
            "Gut gemacht",
            "/teacher/tasks/20/submissions",
            authentication("oidc-teacher"),
            request
        );

        assertThat(view).isEqualTo("redirect:/teacher/tasks/20/submissions");
    }

    @Test
    void submitReview_redirectsToTaskListWhenCommandServiceRejects() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("currentVersion")).thenReturn(null);
        when(teacherTaskCommandService.submitReview(30L, "oidc-teacher", 7L, "Kommentar", null))
            .thenReturn(false);

        String view = controller.submitReview(
            30L,
            7L,
            "Kommentar",
            null,
            authentication("oidc-teacher"),
            request
        );

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
    }

    @Test
    void createTask_delegatesToCommandService() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = new Task();

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        String view = controller.createTask(task, "5", "sql", List.of(10L), authentication("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        verify(teacherTaskCommandService).createTask(task, teacher, "5", "sql", List.of(10L));
    }

    @Test
    void updateTask_delegatesToCommandService() {
        Task task = new Task();
        task.setTitle("Updated");

        String view = controller.updateTask(20L, task, List.of(10L), "5", "sql", "Tutorial");

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        verify(teacherTaskCommandService).updateTask(20L, task, "5", "sql", List.of(10L), "Tutorial");
    }

    @Test
    void deleteTask_delegatesToCommandServiceForResolvedTeacher() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        String view = controller.deleteTask(20L, authentication("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        verify(teacherTaskCommandService).deleteTask(20L, teacher);
    }

    private Principal principal(String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
    }

    private Authentication authentication(String name) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(name);
        return authentication;
    }

    private User teacher(Long id, String name, Group group) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(group));
        return teacher;
    }

    private User student(Long id, String name, Group group) {
        User student = new User();
        student.setId(id);
        student.setName(name);
        student.setGroups(Set.of(group));
        return student;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, User teacher, Group group, UnitTitle unitTitle) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreatedBy(teacher);
        task.setAssignedGroups(Set.of(group));
        task.setUnitTitle(unitTitle);
        return task;
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(User student, Task task, TaskStatus status) {
        UserTask userTask = new UserTask();
        userTask.setId(30L);
        userTask.setUser(student);
        userTask.setTask(task);
        userTask.setStatus(status);
        return userTask;
    }
}
