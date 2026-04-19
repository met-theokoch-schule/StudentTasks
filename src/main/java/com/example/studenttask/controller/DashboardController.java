package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.service.IdentitySyncService;
import com.example.studenttask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private IdentitySyncService identitySyncService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        logAuthenticationSummary(authentication);
        log.debug("Dashboard request started");

        User user = resolveAuthenticatedUser(authentication);
        if (user == null) {
            log.warn("User resolution failed, redirecting to login");
            return "redirect:/login";
        }

        log.debug("User processed: id={}, name={}", user.getId(), user.getName());

        boolean isTeacher = userService.hasTeacherRole(user);
        boolean isStudent = userService.hasStudentRole(user);

        log.debug("Role evaluation results for user {}: isTeacher={}, isStudent={}",
                user.getId(), isTeacher, isStudent);

        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);

        if (isTeacher) {
            return "redirect:/teacher/dashboard";
        } else if (isStudent) {
            return "redirect:/student/dashboard";
        } else {
            // Für alle anderen zeige das allgemeine Dashboard
            return "dashboard";
        }
    }

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            log.warn("Dashboard called without authentication");
            return null;
        }

        String openIdSubject = authentication.getName();
        log.debug("Looking for user with OpenID subject {}", openIdSubject);

        User user = userService.findByOpenIdSubject(openIdSubject).orElse(null);
        if (user != null) {
            return user;
        }

        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            log.info("User with subject {} not found in database, synchronizing from OAuth2", openIdSubject);
            return identitySyncService.syncFromOAuth2User(oauth2User);
        }

        log.warn("Authentication principal is not an OAuth2User: {}",
                authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null");
        return null;
    }

    private void logAuthenticationSummary(Authentication authentication) {
        if (!log.isDebugEnabled()) {
            return;
        }

        log.debug("Authentication class: {}",
                authentication != null ? authentication.getClass().getName() : "null");

        if (authentication == null) {
            return;
        }

        log.debug("Authentication name: {}", authentication.getName());
        log.debug("Authentication principal class: {}",
                authentication.getPrincipal() != null
                        ? authentication.getPrincipal().getClass().getName()
                        : "null");
        log.debug("Authentication authorities: {}", authentication.getAuthorities());
        log.debug("Authentication authenticated flag: {}", authentication.isAuthenticated());
    }
}
