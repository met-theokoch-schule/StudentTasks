package com.example.studenttask.exception;

public class TaskInvariantViolationException extends RuntimeException {

    public TaskInvariantViolationException(String message) {
        super(message);
    }
}
