package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiControllerTest {

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private StudentTaskApiController controller;

    @Test
    void getTaskContent_returnsUnauthorizedWhenUserCannotBeResolved() {
        Authentication authentication = authentication("oidc-subject");
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        ResponseEntity<String> response = controller.getTaskContent(7L, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEmpty();
        verifyNoInteractions(taskService, userTaskService, taskContentService);
    }

    @Test
    void getTaskContent_returnsLatestStoredContent() {
        Authentication authentication = authentication("oidc-subject");
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

        ResponseEntity<String> response = controller.getTaskContent(7L, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("latest-solution");
        verify(taskContentService).getLatestContent(userTask);
    }

    @Test
    void saveTaskContent_usesFindOrCreateAndPersistsDraftContent() {
        Authentication authentication = authentication("oidc-subject");
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

        ResponseEntity<String> response = controller.saveTaskContent(
                7L,
                Map.of("content", "print('ok')"),
                authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ID: 55").contains("Version: 4");
        verify(taskContentService).saveContent(userTask, "print('ok')", false);
    }

    @Test
    void submitTask_submitsProvidedContentWithoutControllerLevelStatusMutation() {
        Authentication authentication = authentication("oidc-subject");
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);

        ResponseEntity<Void> response = controller.submitTask(
                7L,
                Map.of("content", "submitted-solution"),
                authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(taskContentService).submitContent(userTask, "submitted-solution");
    }

    private Authentication authentication(String subject) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(subject);
        return authentication;
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
