package com.example.studenttask.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuthConfigurationGuardFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_PATH = "/oauth2/authorization/iserv";
    private static final String SETUP_PATH = "/oauth/setup";

    private final OAuthConfigurationStatusService oauthConfigurationStatusService;

    public OAuthConfigurationGuardFilter(OAuthConfigurationStatusService oauthConfigurationStatusService) {
        this.oauthConfigurationStatusService = oauthConfigurationStatusService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        return !AUTHORIZATION_PATH.equals(requestPath) || oauthConfigurationStatusService.isReady();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + SETUP_PATH);
    }
}
