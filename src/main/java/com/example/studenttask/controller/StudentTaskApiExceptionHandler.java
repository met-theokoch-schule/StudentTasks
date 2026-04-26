package com.example.studenttask.controller;

import com.example.studenttask.dto.ApiErrorResponseDto;
import com.example.studenttask.exception.ApiInvalidStateException;
import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.TaskNotFoundException;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.exception.TaskInvariantViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = StudentTaskApiController.class)
public class StudentTaskApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiExceptionHandler.class);

    @ExceptionHandler(ApiUnauthorizedException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnauthorized(ApiUnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponseDto("unauthorized", exception.getMessage()));
    }

    @ExceptionHandler({
        ApiNotFoundException.class,
        TaskNotFoundException.class,
        TaskStatusNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponseDto> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponseDto("not_found", exception.getMessage()));
    }

    @ExceptionHandler({
        ApiInvalidStateException.class,
        TaskInvariantViolationException.class
    })
    public ResponseEntity<ApiErrorResponseDto> handleInvalidState(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponseDto("invalid_state", exception.getMessage()));
    }

    @ExceptionHandler({
        AccessDeniedException.class,
        StudentAccessDeniedException.class
    })
    public ResponseEntity<ApiErrorResponseDto> handleForbidden(Exception exception) {
        String message = exception instanceof StudentAccessDeniedException
            ? exception.getMessage()
            : "Zugriff verweigert";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiErrorResponseDto("forbidden", message));
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponseDto> handleBadRequest(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponseDto("bad_request", "Die Anfrage ist ungueltig"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnexpected(Exception exception) {
        log.error("Unexpected error in StudentTask API", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponseDto("internal_error", "Ein unerwarteter Fehler ist aufgetreten"));
    }
}
