package com.example.studenttask.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HomeMvcExceptionHandlerTest {

    private final HomeMvcExceptionHandler exceptionHandler = new HomeMvcExceptionHandler();

    @Test
    void redirectsToLoginWhenAuthenticationIsMissing() {
        String view = exceptionHandler.handleAuthenticationRequired();

        assertThat(view).isEqualTo("redirect:/login?required=true");
    }
}
