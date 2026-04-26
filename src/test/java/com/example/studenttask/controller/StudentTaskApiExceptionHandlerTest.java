package com.example.studenttask.controller;

import com.example.studenttask.dto.ApiErrorResponseDto;
import com.example.studenttask.exception.ApiInvalidStateException;
import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.TaskInvariantViolationException;
import com.example.studenttask.exception.TaskNotFoundException;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTaskApiExceptionHandlerTest {

    private final StudentTaskApiExceptionHandler exceptionHandler = new StudentTaskApiExceptionHandler();

    @Test
    void handleUnauthorized_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleUnauthorized(new ApiUnauthorizedException("Benutzer nicht gefunden"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("unauthorized");
        assertThat(response.getBody().getMessage()).isEqualTo("Benutzer nicht gefunden");
    }

    @Test
    void handleNotFound_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleNotFound(new ApiNotFoundException("Aufgabe nicht gefunden"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("not_found");
        assertThat(response.getBody().getMessage()).isEqualTo("Aufgabe nicht gefunden");
    }

    @Test
    void handleTaskNotFound_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleNotFound(new TaskNotFoundException("Task not found with ID: 7"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("not_found");
        assertThat(response.getBody().getMessage()).isEqualTo("Task not found with ID: 7");
    }

    @Test
    void handleTaskStatusNotFound_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleNotFound(new TaskStatusNotFoundException("Status in_bearbeitung not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("not_found");
        assertThat(response.getBody().getMessage()).isEqualTo("Status in_bearbeitung not found");
    }

    @Test
    void handleInvalidState_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleInvalidState(new ApiInvalidStateException("Statuswechsel nicht erlaubt"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("invalid_state");
        assertThat(response.getBody().getMessage()).isEqualTo("Statuswechsel nicht erlaubt");
    }

    @Test
    void handleTaskInvariantViolation_returnsStandardizedConflictResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleInvalidState(
                new TaskInvariantViolationException("TaskContent version 3 exists already for UserTask 30")
            );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("invalid_state");
        assertThat(response.getBody().getMessage())
            .isEqualTo("TaskContent version 3 exists already for UserTask 30");
    }

    @Test
    void handleAccessDenied_returnsStandardizedForbiddenResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleForbidden(new AccessDeniedException("Access Denied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("forbidden");
        assertThat(response.getBody().getMessage()).isEqualTo("Zugriff verweigert");
    }

    @Test
    void handleStudentAccessDenied_returnsSpecificForbiddenResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleForbidden(new StudentAccessDeniedException("Keine Berechtigung fuer diese Aufgabe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("forbidden");
        assertThat(response.getBody().getMessage()).isEqualTo("Keine Berechtigung fuer diese Aufgabe");
    }

    @Test
    void handleBadRequest_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleBadRequest(new HttpMessageNotReadableException("Malformed JSON"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("bad_request");
        assertThat(response.getBody().getMessage()).isEqualTo("Die Anfrage ist ungueltig");
    }

    @Test
    void handleUnexpected_returnsGenericInternalErrorResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleUnexpected(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("internal_error");
        assertThat(response.getBody().getMessage()).isEqualTo("Ein unerwarteter Fehler ist aufgetreten");
    }
}
