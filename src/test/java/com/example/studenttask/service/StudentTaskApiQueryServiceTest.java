package com.example.studenttask.service;

import com.example.studenttask.exception.ApiUnauthorizedException;
import com.example.studenttask.model.TaskContent;
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
class StudentTaskApiQueryServiceTest {

    @Mock
    private StudentTaskApiAccessService studentTaskApiAccessService;

    @Mock
    private TaskContentService taskContentService;

    @InjectMocks
    private StudentTaskApiQueryService studentTaskApiQueryService;

    @Test
    void getTaskContent_propagatesUnauthorizedWhenAccessCannotBeResolved() {
        when(studentTaskApiAccessService.findUserTask(7L, "oidc-subject"))
            .thenThrow(new ApiUnauthorizedException("Benutzer nicht gefunden"));

        assertThatThrownBy(() -> studentTaskApiQueryService.getTaskContent(7L, "oidc-subject"))
            .isInstanceOf(ApiUnauthorizedException.class)
            .hasMessage("Benutzer nicht gefunden");

        verifyNoInteractions(taskContentService);
    }

    @Test
    void getTaskContent_returnsLatestStoredContent() {
        UserTask userTask = userTask(100L);
        TaskContent latestContent = new TaskContent();
        latestContent.setVersion(3);
        latestContent.setContent("latest-solution");

        when(studentTaskApiAccessService.findUserTask(7L, "oidc-subject")).thenReturn(Optional.of(userTask));
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        String result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result).isEqualTo("latest-solution");
        verify(taskContentService).getLatestContent(userTask);
    }

    @Test
    void getTaskContent_returnsEmptyStringWhenNoUserTaskExists() {
        when(studentTaskApiAccessService.findUserTask(7L, "oidc-subject")).thenReturn(Optional.empty());

        String result = studentTaskApiQueryService.getTaskContent(7L, "oidc-subject");

        assertThat(result).isEmpty();
        verifyNoInteractions(taskContentService);
    }

    private UserTask userTask(Long id) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        return userTask;
    }
}
