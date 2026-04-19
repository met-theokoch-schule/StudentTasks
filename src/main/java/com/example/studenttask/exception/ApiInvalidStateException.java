package com.example.studenttask.exception;

public class ApiInvalidStateException extends RuntimeException {

    public ApiInvalidStateException(String message) {
        super(message);
    }
}
