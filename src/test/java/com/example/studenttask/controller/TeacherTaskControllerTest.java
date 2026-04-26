package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.dto.VersionWithSubmissionStatus;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(null, taskForm, List.of(taskView), List.of(group), List.of(unitTitle));

        when(teacherTaskQueryService.getCreateTaskFormData()).thenReturn(formData);

        Model model = new ExtendedModelMap();
        String view = controller.showCreateTaskForm(model);

        assertThat(view).isEqualTo("teacher/task-create");
        assertThat(model.getAttribute("taskForm")).isSameAs(taskForm);
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
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");
        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(task, taskForm, List.of(taskView), List.of(group), List.of(unitTitle));

        when(teacherTaskQueryService.getEditTaskFormData(20L)).thenReturn(Optional.of(formData));

        Model model = new ExtendedModelMap();
        String view = controller.showEditTaskForm(20L, model);

        assertThat(view).isEqualTo("teacher/task-edit");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("taskForm")).isSameAs(taskForm);
        assertThat(model.getAttribute("taskViews")).isEqualTo(List.of(taskView));
        assertThat(model.getAttribute("groups")).isEqualTo(List.of(group));
        assertThat(model.getAttribute("unitTitles")).isEqualTo(List.of(unitTitle));
    }

    @Test
    void submitReview_delegatesToCommandServiceAndRedirectsToReturnUrl() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("currentVersion")).thenReturn("2");

        String view = controller.submitReview(
            30L,
            7L,
            "Gut gemacht",
            "/teacher/tasks/20/submissions",
            authentication("oidc-teacher"),
            request
        );

        assertThat(view).isEqualTo("redirect:/teacher/tasks/20/submissions");
        verify(teacherTaskCommandService).submitReview(30L, "oidc-teacher", 7L, "Gut gemacht", "2");
    }

    @Test
    void createTask_delegatesToCommandService() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");
        taskForm.setTaskViewId(5L);
        taskForm.setUnitTitleId("sql");
        taskForm.setSelectedGroups(List.of(10L));

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(teacherTaskQueryService.hasTaskView(5L)).thenReturn(true);
        when(teacherTaskQueryService.hasUnitTitle("sql")).thenReturn(true);
        when(teacherTaskQueryService.hasAllGroups(List.of(10L))).thenReturn(true);

        BindingResult bindingResult = new BeanPropertyBindingResult(taskForm, "taskForm");
        String view = controller.createTask(taskForm, bindingResult, authentication("oidc-teacher"), new ExtendedModelMap());

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        verify(teacherTaskCommandService).createTask(teacher, taskForm);
    }

    @Test
    void updateTask_delegatesToCommandService() {
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Updated");
        taskForm.setTaskViewId(5L);
        taskForm.setUnitTitleId("sql");
        taskForm.setSelectedGroups(List.of(10L));
        taskForm.setTutorial("Tutorial");

        when(teacherTaskQueryService.hasTaskView(5L)).thenReturn(true);
        when(teacherTaskQueryService.hasUnitTitle("sql")).thenReturn(true);
        when(teacherTaskQueryService.hasAllGroups(List.of(10L))).thenReturn(true);

        BindingResult bindingResult = new BeanPropertyBindingResult(taskForm, "taskForm");
        String view = controller.updateTask(20L, taskForm, bindingResult, new ExtendedModelMap());

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        verify(teacherTaskCommandService).updateTask(20L, taskForm);
    }

    @Test
    void createTask_rendersFormAgainWhenTaskViewIsInvalid() {
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");
        taskForm.setTaskViewId(999L);

        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(
            null,
            new TeacherTaskFormDto(),
            List.of(),
            List.of(),
            List.of()
        );
        when(teacherTaskQueryService.hasTaskView(999L)).thenReturn(false);
        when(teacherTaskQueryService.hasAllGroups(List.of())).thenReturn(true);
        when(teacherTaskQueryService.getCreateTaskFormData()).thenReturn(formData);

        BindingResult bindingResult = new BeanPropertyBindingResult(taskForm, "taskForm");
        Model model = new ExtendedModelMap();
        String view = controller.createTask(taskForm, bindingResult, mock(Authentication.class), model);

        assertThat(view).isEqualTo("teacher/task-create");
        assertThat(bindingResult.getFieldError("taskViewId")).isNotNull();
        assertThat(model.getAttribute("taskForm")).isSameAs(taskForm);
        verifyNoInteractions(userService);
        verify(teacherTaskCommandService, never()).createTask(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateTask_rendersFormAgainWhenUnitTitleIsInvalid() {
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Updated");
        taskForm.setUnitTitleId("missing");

        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        TeacherTaskFormDataDto formData = new TeacherTaskFormDataDto(task, new TeacherTaskFormDto(), List.of(), List.of(), List.of());

        when(teacherTaskQueryService.hasUnitTitle("missing")).thenReturn(false);
        when(teacherTaskQueryService.hasAllGroups(List.of())).thenReturn(true);
        when(teacherTaskQueryService.getEditTaskFormData(20L)).thenReturn(Optional.of(formData));

        BindingResult bindingResult = new BeanPropertyBindingResult(taskForm, "taskForm");
        Model model = new ExtendedModelMap();
        String view = controller.updateTask(20L, taskForm, bindingResult, model);

        assertThat(view).isEqualTo("teacher/task-edit");
        assertThat(bindingResult.getFieldError("unitTitleId")).isNotNull();
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("taskForm")).isSameAs(taskForm);
        verify(teacherTaskCommandService, never()).updateTask(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
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

    @Test
    void listTasks_throwsAuthenticationExceptionWhenTeacherCannotBeResolved() {
        when(userService.findByOpenIdSubject("missing-teacher")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.listTasks("own", new ExtendedModelMap(), principal("missing-teacher")))
            .isInstanceOf(TeacherAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void viewSubmissionInTaskView_throwsNotFoundExceptionWhenSubmissionDoesNotExist() {
        when(teacherTaskQueryService.getSubmissionContentViewData(30L, 3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.viewSubmissionInTaskView(30L, 3, new ExtendedModelMap()))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Abgabe nicht gefunden");
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
