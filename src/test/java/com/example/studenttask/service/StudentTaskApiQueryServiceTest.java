package com.example.studenttask.service;

import com.example.studenttask.dto.ApiOperationStatus;
import com.example.studenttask.dto.TaskContentLoadResultDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiQueryServiceTest {

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private StudentTaskApiQueryService studentTaskApiQueryService;

    @Test
    void getTaskContent_returnsUnauthorizedWhenUserCannotBeResolved() {
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        TaskContentLoadResultDto result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.UNAUTHORIZED);
        assertThat(result.getContent()).isEmpty();
        verifyNoInteractions(taskService, userTaskService, taskContentService);
    }

    @Test
    void getTaskContent_returnsNotFoundWhenTaskDoesNotExist() {
        User user = user(1L, "Student One");
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.empty());

        TaskContentLoadResultDto result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.NOT_FOUND);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getTaskContent_returnsLatestStoredContent() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);
        TaskContent latestContent = new TaskContent();
        latestContent.setVersion(3);
        latestContent.setContent("latest-solution");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findByUserIdAndTaskId(1L, 7L)).thenReturn(Optional.of(userTask));
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        TaskContentLoadResultDto result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getContent()).isEqualTo("latest-solution");
        verify(taskContentService).getLatestContent(userTask);
    }

    @Test
    void getTaskContent_returnsEmptyStringWhenNoUserTaskExists() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findByUserIdAndTaskId(1L, 7L)).thenReturn(Optional.empty());

        TaskContentLoadResultDto result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result.getStatus()).isEqualTo(ApiOperationStatus.SUCCESS);
        assertThat(result.getContent()).isEmpty();
        verifyNoInteractions(taskContentService);
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
