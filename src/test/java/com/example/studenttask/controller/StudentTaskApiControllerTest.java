package com.example.studenttask.controller;

import com.example.studenttask.dto.TaskContentRequestDto;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.service.StudentTaskApiCommandService;
import com.example.studenttask.service.StudentTaskApiQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskApiControllerTest {

    @Mock
    private StudentTaskApiQueryService studentTaskApiQueryService;

    @Mock
    private StudentTaskApiCommandService studentTaskApiCommandService;

    @InjectMocks
    private StudentTaskApiController controller;

    @Test
    void getTaskContent_returnsLatestStoredContent() {
        Authentication authentication = authentication("oidc-subject");
        when(studentTaskApiQueryService.getTaskContent(7L, "oidc-subject"))
            .thenReturn("latest-solution");

        ResponseEntity<String> response = controller.getTaskContent(7L, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("latest-solution");
        verify(studentTaskApiQueryService).getTaskContent(7L, "oidc-subject");
    }

    @Test
    void saveUserTaskContent_delegatesTeacherSaveAndReturnsOk() {
        Authentication authentication = authentication("oidc-teacher");

        ResponseEntity<String> response = controller.saveUserTaskContent(
            100L,
            new TaskContentRequestDto("teacher-feedback"),
            authentication
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Content saved successfully");
        verify(studentTaskApiCommandService).saveTeacherTaskContent(100L, "teacher-feedback", "oidc-teacher");
    }

    @Test
    void saveTaskContent_usesFindOrCreateAndPersistsDraftContent() {
        Authentication authentication = authentication("oidc-subject");
        TaskContent savedContent = new TaskContent();
        savedContent.setId(55L);
        savedContent.setVersion(4);
        savedContent.setContent("print('ok')");

        when(studentTaskApiCommandService.saveTaskContent(7L, "oidc-subject", "print('ok')"))
            .thenReturn(savedContent);

        ResponseEntity<String> response = controller.saveTaskContent(
            7L,
            new TaskContentRequestDto("print('ok')"),
            authentication
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ID: 55").contains("Version: 4");
        verify(studentTaskApiCommandService).saveTaskContent(7L, "oidc-subject", "print('ok')");
    }

    @Test
    void submitTask_submitsProvidedContentWithoutControllerLevelStatusMutation() {
        Authentication authentication = authentication("oidc-subject");

        ResponseEntity<Void> response = controller.submitTask(
            7L,
            new TaskContentRequestDto("submitted-solution"),
            authentication
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(studentTaskApiCommandService).submitTask(7L, "oidc-subject", "submitted-solution");
    }

    @Test
    void submitTask_allowsMissingRequestBody() {
        Authentication authentication = authentication("oidc-subject");

        ResponseEntity<Void> response = controller.submitTask(7L, null, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(studentTaskApiCommandService).submitTask(7L, "oidc-subject", null);
    }

    private Authentication authentication(String subject) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(subject);
        return authentication;
    }
}
