package com.example.studenttask.controller;

import com.example.studenttask.dto.ApiErrorResponseDto;
import com.example.studenttask.exception.ApiInvalidStateException;
import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void handleInvalidState_returnsStandardizedResponse() {
        ResponseEntity<ApiErrorResponseDto> response =
            exceptionHandler.handleInvalidState(new ApiInvalidStateException("Statuswechsel nicht erlaubt"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("invalid_state");
        assertThat(response.getBody().getMessage()).isEqualTo("Statuswechsel nicht erlaubt");
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
