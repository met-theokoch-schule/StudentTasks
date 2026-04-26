package com.example.studenttask.exception;

public class UserAuthenticationRequiredException extends RuntimeException {

    public UserAuthenticationRequiredException(String message) {
        super(message);
    }
}
