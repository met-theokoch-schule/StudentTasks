package com.example.studenttask.controller;

import com.example.studenttask.exception.StudentResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class DebugMvcExceptionHandlerTest {

    private final DebugMvcExceptionHandler exceptionHandler = new DebugMvcExceptionHandler();

    @Test
    void redirectsToLoginWhenAuthenticationIsMissing() {
        String view = exceptionHandler.handleAuthenticationRequired();

        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void returnsNotFoundViewWithDebugReturnLinkForMissingDebugResource() {
        Model model = new ExtendedModelMap();

        String view = exceptionHandler.handleResourceNotFound(
            new StudentResourceNotFoundException("Version 4 für diese Aufgabe nicht gefunden"),
            model
        );

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Version 4 für diese Aufgabe nicht gefunden");
        assertThat(model.getAttribute("returnUrl")).isEqualTo("/debug");
        assertThat(model.getAttribute("returnLabel")).isEqualTo("Zurueck zur Debug-Seite");
    }
}
