package com.example.studenttask.exception;

public class TeacherAuthenticationRequiredException extends RuntimeException {

    public TeacherAuthenticationRequiredException(String message) {
        super(message);
    }
}
