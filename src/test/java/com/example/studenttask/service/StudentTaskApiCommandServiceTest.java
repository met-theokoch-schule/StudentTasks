package com.example.studenttask.service;

import com.example.studenttask.dto.ApiOperationStatus;
import com.example.studenttask.dto.TaskContentCommandResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiCommandServiceTest {

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private StudentTaskApiCommandService studentTaskApiCommandService;

    @Test
    void saveTeacherTaskContent_returnsNotFoundWhenUserTaskDoesNotExist() {
        when(userTaskService.findById(100L)).thenReturn(Optional.empty());

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.saveTeacherTaskContent(100L, "answer", "oidc-teacher");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.NOT_FOUND);
    }

    @Test
    void saveTaskContent_returnsUnauthorizedWhenUserCannotBeResolved() {
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.UNAUTHORIZED);
    }

    @Test
    void saveTaskContent_usesFindOrCreateAndPersistsDraftContent() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);
        TaskContent savedContent = new TaskContent();
        savedContent.setId(55L);
        savedContent.setVersion(4);
        savedContent.setContent("print('ok')");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);
        when(taskContentService.saveContent(userTask, "print('ok')", false)).thenReturn(savedContent);

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getTaskContent()).isSameAs(savedContent);
        verify(taskContentService).saveContent(userTask, "print('ok')", false);
    }

    @Test
    void submitTask_submitsProvidedContent() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);
        TaskContent submittedContent = new TaskContent();
        submittedContent.setVersion(5);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);
        when(taskContentService.submitContent(userTask, "submitted-solution")).thenReturn(submittedContent);

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.submitTask(7L, "oidc-subject", "submitted-solution");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getTaskContent()).isSameAs(submittedContent);
        verify(taskContentService).submitContent(userTask, "submitted-solution");
    }

    @Test
    void submitTask_submitsLatestSavedContentWhenRequestBodyIsMissing() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);
        TaskContent latestContent = new TaskContent();
        latestContent.setContent("latest-solution");
        TaskContent submittedContent = new TaskContent();
        submittedContent.setVersion(6);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));
        when(taskContentService.submitContent(userTask, "latest-solution")).thenReturn(submittedContent);

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.submitTask(7L, "oidc-subject", null);

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getTaskContent()).isSameAs(submittedContent);
        verify(taskContentService).submitContent(userTask, "latest-solution");
    }

    @Test
    void submitTask_returnsSuccessWithoutPersistingWhenNoContentExists() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.empty());

        TaskContentCommandResultDto result =
            studentTaskApiCommandService.submitTask(7L, "oidc-subject", null);

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getTaskContent()).isNull();
        verify(taskContentService, never()).submitContent(any(UserTask.class), any(String.class));
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Task task(Long id, String title) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        return task;
    }

    private UserTask userTask(Long id, User user, Task task) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setUser(user);
        userTask.setTask(task);
        return userTask;
    }
}
