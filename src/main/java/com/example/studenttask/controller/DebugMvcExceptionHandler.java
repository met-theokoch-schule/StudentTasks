package com.example.studenttask.controller;

import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = DebugController.class)
public class DebugMvcExceptionHandler {

    private static final String DEBUG_HOME_URL = "/debug";
    private static final String DEBUG_HOME_LABEL = "Zurueck zur Debug-Seite";

    @ExceptionHandler(UserAuthenticationRequiredException.class)
    public String handleAuthenticationRequired() {
        return "redirect:/login";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StudentResourceNotFoundException.class)
    public String handleResourceNotFound(StudentResourceNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("returnUrl", DEBUG_HOME_URL);
        model.addAttribute("returnLabel", DEBUG_HOME_LABEL);
        return "error/404";
    }
}
