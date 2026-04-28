package com.example.studenttask.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class RedirectTargetService {

    public String buildRelativeCurrentUrl(HttpServletRequest request) {
        String currentUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            currentUrl += "?" + queryString;
        }
        return currentUrl;
    }

    public String resolveReturnUrl(String requestedReturnUrl, HttpServletRequest request, String fallbackPath) {
        String sanitizedReturnUrl = sanitizeLocalTarget(requestedReturnUrl, request);
        if (sanitizedReturnUrl != null) {
            return sanitizedReturnUrl;
        }

        String referer = request != null ? request.getHeader("Referer") : null;
        String sanitizedReferer = sanitizeLocalTarget(referer, request);
        if (sanitizedReferer != null) {
            return sanitizedReferer;
        }

        return fallbackPath;
    }

    public String resolveRedirectTarget(String requestedReturnUrl, HttpServletRequest request, String fallbackPath) {
        String sanitizedReturnUrl = sanitizeLocalTarget(requestedReturnUrl, request);
        return sanitizedReturnUrl != null ? sanitizedReturnUrl : fallbackPath;
    }

    private String sanitizeLocalTarget(String target, HttpServletRequest request) {
        if (target == null) {
            return null;
        }

        String trimmedTarget = target.trim();
        if (trimmedTarget.isEmpty() || containsLineBreak(trimmedTarget)) {
            return null;
        }

        try {
            URI uri = URI.create(trimmedTarget);
            if (uri.isAbsolute()) {
                if (!isSameOrigin(uri, request)) {
                    return null;
                }
                return toRelativeTarget(uri.getRawPath(), uri.getRawQuery());
            }

            if (!trimmedTarget.startsWith("/") || trimmedTarget.startsWith("//") || trimmedTarget.startsWith("/\\")) {
                return null;
            }
            return trimmedTarget;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean containsLineBreak(String value) {
        return value.contains("\n") || value.contains("\r");
    }

    private boolean isSameOrigin(URI uri, HttpServletRequest request) {
        if (request == null || uri.getScheme() == null || uri.getHost() == null) {
            return false;
        }

        return request.getScheme().equalsIgnoreCase(uri.getScheme())
            && request.getServerName().equalsIgnoreCase(uri.getHost())
            && normalizePort(request.getScheme(), request.getServerPort()) == normalizePort(uri.getScheme(), uri.getPort());
    }

    private int normalizePort(String scheme, int port) {
        if (port != -1) {
            return port;
        }
        if ("https".equalsIgnoreCase(scheme)) {
            return 443;
        }
        if ("http".equalsIgnoreCase(scheme)) {
            return 80;
        }
        return -1;
    }

    private String toRelativeTarget(String path, String query) {
        if (path == null || path.isBlank() || !path.startsWith("/")) {
            return null;
        }
        if (query == null || query.isBlank()) {
            return path;
        }
        return path + "?" + query;
    }
}
