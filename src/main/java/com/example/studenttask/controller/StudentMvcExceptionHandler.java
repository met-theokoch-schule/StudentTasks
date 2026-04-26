package com.example.studenttask.controller;

import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = {
        DashboardController.class,
        StudentController.class,
        TaskController.class
})
public class StudentMvcExceptionHandler {

    private static final String STUDENT_DASHBOARD_URL = "/student/dashboard";
    private static final String STUDENT_DASHBOARD_LABEL = "Zurueck zum Schueler-Dashboard";

    @ExceptionHandler(UserAuthenticationRequiredException.class)
    public String handleAuthenticationRequired() {
        return "redirect:/login";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StudentResourceNotFoundException.class)
    public String handleResourceNotFound(StudentResourceNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("returnUrl", STUDENT_DASHBOARD_URL);
        model.addAttribute("returnLabel", STUDENT_DASHBOARD_LABEL);
        return "error/404";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(StudentAccessDeniedException.class)
    public String handleAccessDenied(StudentAccessDeniedException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("returnUrl", STUDENT_DASHBOARD_URL);
        model.addAttribute("returnLabel", STUDENT_DASHBOARD_LABEL);
        return "access-denied";
    }
}
