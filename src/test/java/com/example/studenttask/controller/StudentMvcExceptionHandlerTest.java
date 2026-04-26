package com.example.studenttask.controller;

import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMvcExceptionHandlerTest {

    private final StudentMvcExceptionHandler exceptionHandler = new StudentMvcExceptionHandler();

    @Test
    void redirectsToLoginWhenAuthenticationIsMissing() {
        String view = exceptionHandler.handleAuthenticationRequired();

        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void returnsNotFoundViewWithMessageForMissingStudentResource() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new StudentResourceNotFoundException("Aufgabe nicht gefunden"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Aufgabe nicht gefunden");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/student/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Schueler-Dashboard");
    }

    @Test
    void returnsAccessDeniedViewWithMessageForStudentAccessErrors() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleAccessDenied(
            new StudentAccessDeniedException("Keine Berechtigung für diese Aufgabe"),
            model
        );

        assertThat(view).isEqualTo("access-denied");
        assertThat(model.getAttribute("message")).isEqualTo("Keine Berechtigung für diese Aufgabe");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/student/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Schueler-Dashboard");
    }
}
