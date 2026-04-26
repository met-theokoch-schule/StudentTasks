package com.example.studenttask.exception;

public class StudentAccessDeniedException extends RuntimeException {

    public StudentAccessDeniedException(String message) {
        super(message);
    }
}
