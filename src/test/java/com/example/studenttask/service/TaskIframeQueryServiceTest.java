package com.example.studenttask.service;

import com.example.studenttask.dto.TaskIframeViewResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskIframeQueryServiceTest {

    @Mock
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskIframeQueryService taskIframeQueryService;

    @Test
    void getTaskIframeViewData_usesLatestContentAndResolvedTaskView() {
        Task task = task(40L, "Task description", "Default content");
        TaskView taskView = taskView(7L, "taskviews/task-view");
        task.setTaskView(taskView);

        User student = user(1L);
        UserTask userTask = userTask(99L, student, task);

        TaskContent latestContent = new TaskContent();
        latestContent.setContent("Saved content");

        when(studentTaskViewSupportService.findTask(40L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskViewSupportService.findOrCreateUserTask(student, task)).thenReturn(userTask);
        when(studentTaskViewSupportService.getRequestedContent(userTask, null)).thenReturn(latestContent);
        when(studentTaskViewSupportService.resolveCurrentContent(task, latestContent, false))
            .thenReturn("Saved content");
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(true);

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(40L, "oidc-subject", false, null);

        assertThat(result.isRedirect()).isFalse();
        assertThat(result.getViewData()).isNotNull();
        assertThat(result.getViewData().getTask()).isSameAs(task);
        assertThat(result.getViewData().getTaskView()).isSameAs(taskView);
        assertThat(result.getViewData().getUserTask()).isSameAs(userTask);
        assertThat(result.getViewData().getCurrentContent()).isEqualTo("Saved content");
        assertThat(result.getViewData().getRenderedDescription()).isEqualTo("Task description");
        assertThat(result.getViewData().isTeacherView()).isFalse();
    }

    @Test
    void getTaskIframeViewData_usesDefaultSubmissionWhenVersionContentIsMissing() {
        Task task = task(41L, "Task description", "Default content");
        task.setTaskView(taskView(8L, "taskviews/task-view"));

        User student = user(1L);
        UserTask userTask = userTask(100L, student, task);

        when(studentTaskViewSupportService.findTask(41L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskViewSupportService.findOrCreateUserTask(student, task)).thenReturn(userTask);
        when(studentTaskViewSupportService.getRequestedContent(userTask, 3)).thenReturn(null);
        when(studentTaskViewSupportService.resolveCurrentContent(task, null, false)).thenReturn("Default content");
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(task.getTaskView());
        when(studentTaskViewSupportService.hasRenderableTemplate(task.getTaskView())).thenReturn(true);

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(41L, "oidc-subject", false, 3);

        assertThat(result.isRedirect()).isFalse();
        assertThat(result.getViewData().getCurrentContent()).isEqualTo("Default content");
    }

    @Test
    void getTaskIframeViewData_redirectsToLoginWhenStudentUserMissing() {
        Task task = task(42L, "Task description", null);

        when(studentTaskViewSupportService.findTask(42L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(42L, "oidc-subject", false, null);

        assertThat(result.isRedirect()).isTrue();
        assertThat(result.getRedirectPath()).isEqualTo("redirect:/login");
    }

    @Test
    void getTaskIframeViewData_redirectsToTeacherDashboardWhenTeacherUserMissing() {
        Task task = task(43L, "Task description", null);

        when(studentTaskViewSupportService.findTask(43L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(43L, "oidc-subject", true, null);

        assertThat(result.isRedirect()).isTrue();
        assertThat(result.getRedirectPath()).isEqualTo("redirect:/teacher/dashboard");
    }

    @Test
    void getTaskIframeViewData_redirectsWhenTaskViewTemplateIsBlank() {
        Task task = task(44L, "Task description", null);
        task.setTaskView(taskView(9L, " "));

        User student = user(1L);
        UserTask userTask = userTask(101L, student, task);

        when(studentTaskViewSupportService.findTask(44L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(studentTaskViewSupportService.findOrCreateUserTask(student, task)).thenReturn(userTask);
        when(studentTaskViewSupportService.getRequestedContent(userTask, null)).thenReturn(null);
        when(studentTaskViewSupportService.resolveCurrentContent(task, null, false)).thenReturn("");
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(task.getTaskView());
        when(studentTaskViewSupportService.hasRenderableTemplate(task.getTaskView())).thenReturn(false);

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(44L, "oidc-subject", true, null);

        assertThat(result.isRedirect()).isTrue();
        assertThat(result.getRedirectPath()).isEqualTo("redirect:/teacher/dashboard");
    }

    @Test
    void getTaskIframeViewData_redirectsToStudentDashboardWhenTaskIsMissing() {
        when(studentTaskViewSupportService.findTask(45L)).thenReturn(Optional.empty());

        TaskIframeViewResultDto result =
            taskIframeQueryService.getTaskIframeViewData(45L, "oidc-subject", false, null);

        assertThat(result.isRedirect()).isTrue();
        assertThat(result.getRedirectPath()).isEqualTo("redirect:/student/dashboard");
    }

    private Task task(Long id, String description, String defaultSubmission) {
        Task task = new Task();
        task.setId(id);
        task.setDescription(description);
        task.setDefaultSubmission(defaultSubmission);
        return task;
    }

    private TaskView taskView(Long id, String templatePath) {
        TaskView taskView = new TaskView();
        taskView.setId(id);
        taskView.setTemplatePath(templatePath);
        return taskView;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private UserTask userTask(Long id, User user, Task task) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setUser(user);
        userTask.setTask(task);
        return userTask;
    }
}
