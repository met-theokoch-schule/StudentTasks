package com.example.studenttask.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedirectTargetServiceTest {

    private final RedirectTargetService redirectTargetService = new RedirectTargetService();

    @Test
    void buildRelativeCurrentUrl_usesRequestUriAndQueryString() {
        HttpServletRequest request = request("http", "localhost", 8080);
        when(request.getRequestURI()).thenReturn("/teacher/tasks/20/submissions");
        when(request.getQueryString()).thenReturn("filter=open");

        assertThat(redirectTargetService.buildRelativeCurrentUrl(request))
            .isEqualTo("/teacher/tasks/20/submissions?filter=open");
    }

    @Test
    void resolveReturnUrl_prefersRelativeExplicitReturnUrl() {
        HttpServletRequest request = request("http", "localhost", 8080);

        assertThat(redirectTargetService.resolveReturnUrl(
            "/teacher/tasks/20/submissions?filter=open",
            request,
            "/teacher/tasks"
        )).isEqualTo("/teacher/tasks/20/submissions?filter=open");
    }

    @Test
    void resolveReturnUrl_normalizesSameOriginRefererToRelativePath() {
        HttpServletRequest request = request("http", "localhost", 8080);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/teacher/tasks/20/submissions?filter=open");

        assertThat(redirectTargetService.resolveReturnUrl(null, request, "/teacher/tasks"))
            .isEqualTo("/teacher/tasks/20/submissions?filter=open");
    }

    @Test
    void resolveReturnUrl_rejectsExternalRefererAndFallsBack() {
        HttpServletRequest request = request("http", "localhost", 8080);
        when(request.getHeader("Referer")).thenReturn("https://evil.example/steal");

        assertThat(redirectTargetService.resolveReturnUrl(null, request, "/teacher/tasks"))
            .isEqualTo("/teacher/tasks");
    }

    @Test
    void resolveRedirectTarget_rejectsExternalReturnUrlAndFallsBack() {
        HttpServletRequest request = request("http", "localhost", 8080);

        assertThat(redirectTargetService.resolveRedirectTarget(
            "https://evil.example/steal",
            request,
            "/teacher/submissions/30"
        )).isEqualTo("/teacher/submissions/30");
    }

    @Test
    void resolveRedirectTarget_rejectsSchemeRelativeReturnUrlAndFallsBack() {
        HttpServletRequest request = request("http", "localhost", 8080);

        assertThat(redirectTargetService.resolveRedirectTarget(
            "//evil.example/steal",
            request,
            "/teacher/submissions/30"
        )).isEqualTo("/teacher/submissions/30");
    }

    @Test
    void resolveRedirectTarget_normalizesSameOriginAbsoluteReturnUrl() {
        HttpServletRequest request = request("https", "example.invalid", 443);

        assertThat(redirectTargetService.resolveRedirectTarget(
            "https://example.invalid/teacher/tasks/20/submissions?filter=open",
            request,
            "/teacher/submissions/30"
        )).isEqualTo("/teacher/tasks/20/submissions?filter=open");
    }

    private HttpServletRequest request(String scheme, String host, int port) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(host);
        when(request.getServerPort()).thenReturn(port);
        return request;
    }
}
