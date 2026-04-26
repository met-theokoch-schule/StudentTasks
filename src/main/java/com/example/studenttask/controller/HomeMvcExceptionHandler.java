package com.example.studenttask.controller;

import com.example.studenttask.exception.UserAuthenticationRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = HomeController.class)
public class HomeMvcExceptionHandler {

    @ExceptionHandler(UserAuthenticationRequiredException.class)
    public String handleAuthenticationRequired() {
        return "redirect:/login?required=true";
    }
}
