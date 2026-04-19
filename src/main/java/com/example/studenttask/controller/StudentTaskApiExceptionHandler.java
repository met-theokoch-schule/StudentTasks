package com.example.studenttask.controller;

import com.example.studenttask.dto.ApiErrorResponseDto;
import com.example.studenttask.exception.ApiInvalidStateException;
import com.example.studenttask.exception.ApiNotFoundException;
import com.example.studenttask.exception.ApiUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = StudentTaskApiController.class)
public class StudentTaskApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiExceptionHandler.class);

    @ExceptionHandler(ApiUnauthorizedException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnauthorized(ApiUnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponseDto("unauthorized", exception.getMessage()));
    }

    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleNotFound(ApiNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponseDto("not_found", exception.getMessage()));
    }

    @ExceptionHandler(ApiInvalidStateException.class)
    public ResponseEntity<ApiErrorResponseDto> handleInvalidState(ApiInvalidStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponseDto("invalid_state", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnexpected(Exception exception) {
        log.error("Unexpected error in StudentTask API", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponseDto("internal_error", "Ein unerwarteter Fehler ist aufgetreten"));
    }
}
