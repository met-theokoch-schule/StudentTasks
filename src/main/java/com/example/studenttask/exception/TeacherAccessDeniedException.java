package com.example.studenttask.exception;

public class TeacherAccessDeniedException extends RuntimeException {

    public TeacherAccessDeniedException(String message) {
        super(message);
    }
}
