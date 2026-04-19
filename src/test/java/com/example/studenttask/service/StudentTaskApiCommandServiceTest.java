package com.example.studenttask.service;

import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiCommandServiceTest {

    @Mock
    private StudentTaskApiAccessService studentTaskApiAccessService;

    @Mock
    private TaskContentService taskContentService;

    @InjectMocks
    private StudentTaskApiCommandService studentTaskApiCommandService;

    @Test
    void saveTeacherTaskContent_throwsNotFoundWhenUserTaskDoesNotExist() {
        when(studentTaskApiAccessService.requireUserTask(100L))
            .thenThrow(new ApiNotFoundException("UserTask nicht gefunden"));

        assertThatThrownBy(() -> studentTaskApiCommandService.saveTeacherTaskContent(100L, "answer", "oidc-teacher"))
            .isInstanceOf(ApiNotFoundException.class)
            .hasMessage("UserTask nicht gefunden");
    }

    @Test
    void saveTeacherTaskContent_savesDraftContentForExistingUserTask() {
        UserTask userTask = userTask(100L, 1L, 7L);
        TaskContent savedContent = new TaskContent();
        savedContent.setId(55L);
        savedContent.setVersion(4);

        when(studentTaskApiAccessService.requireUserTask(100L)).thenReturn(userTask);
        when(taskContentService.saveContent(userTask, "answer", false)).thenReturn(savedContent);

        TaskContent result = studentTaskApiCommandService.saveTeacherTaskContent(100L, "answer", "oidc-teacher");

        assertThat(result).isSameAs(savedContent);
        verify(taskContentService).saveContent(userTask, "answer", false);
    }

    @Test
    void saveTaskContent_throwsUnauthorizedWhenUserCannotBeResolved() {
        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject"))
            .thenThrow(new ApiUnauthorizedException("Benutzer nicht gefunden"));

        assertThatThrownBy(() -> studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')"))
            .isInstanceOf(ApiUnauthorizedException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void saveTaskContent_throwsNotFoundWhenTaskDoesNotExist() {
        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject"))
            .thenThrow(new ApiNotFoundException("Aufgabe nicht gefunden"));

        assertThatThrownBy(() -> studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')"))
            .isInstanceOf(ApiNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
    }

    @Test
    void saveTaskContent_usesFindOrCreateAndPersistsDraftContent() {
        UserTask userTask = userTask(100L, 1L, 7L);
        TaskContent savedContent = new TaskContent();
        savedContent.setId(55L);
        savedContent.setVersion(4);
        savedContent.setContent("print('ok')");

        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject")).thenReturn(userTask);
        when(taskContentService.saveContent(userTask, "print('ok')", false)).thenReturn(savedContent);

        TaskContent result = studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')");

        assertThat(result).isSameAs(savedContent);
        verify(taskContentService).saveContent(userTask, "print('ok')", false);
    }

    @Test
    void submitTask_throwsUnauthorizedWhenUserCannotBeResolved() {
        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject"))
            .thenThrow(new ApiUnauthorizedException("Benutzer nicht gefunden"));

        assertThatThrownBy(() -> studentTaskApiCommandService.submitTask(7L, "oidc-subject", "submitted-solution"))
            .isInstanceOf(ApiUnauthorizedException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void submitTask_submitsProvidedContent() {
        UserTask userTask = userTask(100L, 1L, 7L);

        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject")).thenReturn(userTask);

        studentTaskApiCommandService.submitTask(7L, "oidc-subject", "submitted-solution");

        verify(taskContentService).submitContent(userTask, "submitted-solution");
        verify(taskContentService, never()).getLatestContent(userTask);
    }

    @Test
    void submitTask_submitsLatestSavedContentWhenRequestBodyIsMissing() {
        UserTask userTask = userTask(100L, 1L, 7L);
        TaskContent latestContent = new TaskContent();
        latestContent.setContent("latest-solution");

        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject")).thenReturn(userTask);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        studentTaskApiCommandService.submitTask(7L, "oidc-subject", null);

        verify(taskContentService).submitContent(userTask, "latest-solution");
    }

    @Test
    void submitTask_doesNothingWhenNoContentExists() {
        UserTask userTask = userTask(100L, 1L, 7L);

        when(studentTaskApiAccessService.findOrCreateUserTask(7L, "oidc-subject")).thenReturn(userTask);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.empty());

        studentTaskApiCommandService.submitTask(7L, "oidc-subject", null);

        verify(taskContentService, never()).submitContent(any(UserTask.class), anyString());
    }

    private UserTask userTask(Long id, Long userId, Long taskId) {
        User user = new User();
        user.setId(userId);

        Task task = new Task();
        task.setId(taskId);

        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setUser(user);
        userTask.setTask(task);
        return userTask;
    }
}
