package com.example.studenttask.service;

import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiAccessServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @InjectMocks
    private StudentTaskApiAccessService studentTaskApiAccessService;

    @Test
    void findUserTask_throwsUnauthorizedWhenUserCannotBeResolved() {
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskApiAccessService.findUserTask(7L, "oidc-subject"))
            .isInstanceOf(ApiUnauthorizedException.class)
            .hasMessage("Benutzer nicht gefunden");

        verifyNoInteractions(taskService, userTaskService);
    }

    @Test
    void findUserTask_throwsNotFoundWhenTaskDoesNotExist() {
        User user = user(1L, "Student One");

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskApiAccessService.findUserTask(7L, "oidc-subject"))
            .isInstanceOf(ApiNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
    }

    @Test
    void findUserTask_returnsResolvedUserTask() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findByUserIdAndTaskId(1L, 7L)).thenReturn(Optional.of(userTask));

        Optional<UserTask> result = studentTaskApiAccessService.findUserTask(7L, "oidc-subject");

        assertThat(result).contains(userTask);
    }

    @Test
    void findOrCreateUserTask_usesResolvedUserAndTask() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(user));
        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);

        UserTask result = studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject");

        assertThat(result).isSameAs(userTask);
        verify(userTaskService).findOrCreateUserTask(user, task);
    }

    @Test
    void requireUserTask_throwsNotFoundWhenUserTaskMissing() {
        when(userTaskService.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskApiAccessService.requireUserTask(100L))
            .isInstanceOf(ApiNotFoundException.class)
            .hasMessage("UserTask nicht gefunden");
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
