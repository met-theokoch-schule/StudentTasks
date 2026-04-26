package com.example.studenttask.controller;

import com.example.studenttask.exception.TaskContentVersionNotFoundException;
import com.example.studenttask.exception.TaskNotFoundException;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.exception.TeacherAccessDeniedException;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherMvcExceptionHandlerTest {

    private final TeacherMvcExceptionHandler exceptionHandler = new TeacherMvcExceptionHandler();

    @Test
    void redirectsToLoginWhenTeacherAuthenticationIsMissing() {
        String view = exceptionHandler.handleAuthenticationRequired();

        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void returnsNotFoundViewWithMessageForMissingTeacherResource() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new TeacherResourceNotFoundException("Aufgabe nicht gefunden"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Aufgabe nicht gefunden");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/teacher/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Lehrer-Dashboard");
    }

    @Test
    void returnsNotFoundViewWithMessageForMissingSubmissionVersion() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new TaskContentVersionNotFoundException("Abgabeversion nicht gefunden"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Abgabeversion nicht gefunden");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/teacher/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Lehrer-Dashboard");
    }

    @Test
    void returnsNotFoundViewWithMessageForMissingTaskStatus() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new TaskStatusNotFoundException("Status not found"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Status not found");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/teacher/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Lehrer-Dashboard");
    }

    @Test
    void returnsNotFoundViewWithMessageForMissingTask() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new TaskNotFoundException("Task not found with ID: 11"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Task not found with ID: 11");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/teacher/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Lehrer-Dashboard");
    }

    @Test
    void returnsAccessDeniedViewWithMessageForTeacherAccessErrors() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleAccessDenied(
            new TeacherAccessDeniedException("Zugriff auf diese Aufgabe verweigert"),
            model
        );

        assertThat(view).isEqualTo("access-denied");
        assertThat(model.getAttribute("message")).isEqualTo("Zugriff auf diese Aufgabe verweigert");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/teacher/dashboard");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zum Lehrer-Dashboard");
    }
}
