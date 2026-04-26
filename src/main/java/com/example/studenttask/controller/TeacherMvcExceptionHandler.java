package com.example.studenttask.controller;

import com.example.studenttask.exception.TaskContentVersionNotFoundException;
import com.example.studenttask.exception.TaskNotFoundException;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.exception.TeacherAccessDeniedException;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = {
        TeacherController.class,
        TeacherGroupController.class,
        TeacherTaskController.class
})
public class TeacherMvcExceptionHandler {

    private static final String TEACHER_DASHBOARD_URL = "/teacher/dashboard";
    private static final String TEACHER_DASHBOARD_LABEL = "Zurueck zum Lehrer-Dashboard";

    @ExceptionHandler(TeacherAuthenticationRequiredException.class)
    public String handleAuthenticationRequired() {
        return "redirect:/login";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        TeacherResourceNotFoundException.class,
        TaskContentVersionNotFoundException.class,
        TaskNotFoundException.class,
        TaskStatusNotFoundException.class
    })
    public String handleResourceNotFound(RuntimeException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("returnUrl", TEACHER_DASHBOARD_URL);
        model.addAttribute("returnLabel", TEACHER_DASHBOARD_LABEL);
        return "error/404";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TeacherAccessDeniedException.class)
    public String handleAccessDenied(TeacherAccessDeniedException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("returnUrl", TEACHER_DASHBOARD_URL);
        model.addAttribute("returnLabel", TEACHER_DASHBOARD_LABEL);
        return "access-denied";
    }
}
