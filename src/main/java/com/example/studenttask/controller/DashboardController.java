package com.example.studenttask.controller;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        logAuthenticationDetails(authentication);
        log.debug("Dashboard request started");

        // Benutzer aus der Datenbank laden oder aktualisieren
        String openIdSubject = authentication.getName();
        log.debug("Looking for user with OpenID subject {}", openIdSubject);

        User user = userService.findUserByOpenIdSubject(openIdSubject);

        // Immer createOrUpdateUserFromOAuth2 aufrufen, um Gruppen zu synchronisieren
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            if (user == null) {
                log.info("User with subject {} not found, creating from OAuth2 data", openIdSubject);
                user = userService.createOrUpdateUserFromOAuth2(oauth2User);
                log.debug("User created with id {}", user != null ? user.getId() : null);
            } else {
                log.debug("Synchronizing existing user {} from OAuth2", user.getId());
                user = userService.createOrUpdateUserFromOAuth2(oauth2User);
                log.debug("User updated with id {}", user.getId());
            }
        } else {
            log.warn("Authentication is not an OAuth2AuthenticationToken: {}",
                    authentication != null ? authentication.getClass().getName() : "null");
        }

        if (user == null) {
            log.warn("User creation or update failed, redirecting to login");
            return "redirect:/login";
        }

        log.debug("User processed: id={}, name={}", user.getId(), user.getName());

        // Role-based flags
        if (log.isDebugEnabled() && user.getRoles() != null) {
            log.debug("Evaluating {} role(s) for user {}", user.getRoles().size(), user.getId());
            for (Role role : user.getRoles()) {
                log.debug("Found role '{}'", role.getName());
            }
        }

        boolean isTeacher = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> {
                    String roleName = role.getName();
                    return "ROLE_TEACHER".equals(roleName) ||
                            "teacher".equals(roleName) ||
                            "lehrer".equals(roleName) ||
                            roleName.toLowerCase().contains("teacher") ||
                            roleName.toLowerCase().contains("lehrer");
                });

        boolean isStudent = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> {
                    String roleName = role.getName();
                    return "ROLE_STUDENT".equals(roleName) ||
                            "student".equals(roleName) ||
                            "schueler".equals(roleName) ||
                            "schüler".equals(roleName) ||
                            roleName.toLowerCase().contains("student") ||
                            roleName.toLowerCase().contains("schueler") ||
                            roleName.toLowerCase().contains("schüler");
                });

        log.debug("Role evaluation results for user {}: isTeacher={}, isStudent={}",
                user.getId(), isTeacher, isStudent);

        try {
            List<Group> groups = groupService.getGroupsForUser(user);
            log.debug("Fetched {} group(s) for user {}",
                    groups == null ? 0 : groups.size(), user.getId());

            if (log.isDebugEnabled() && groups != null) {
                for (Group group : groups) {
                    log.debug("Group assigned to user {}: id={}, name='{}'",
                            user.getId(), group.getId(), group.getName());
                }
            }
        } catch (Exception e) {
            log.error("Exception while fetching groups for user {}", user.getId(), e);
        }

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

    private void logAuthenticationDetails(Authentication authentication) {
        if (!log.isDebugEnabled()) {
            return;
        }

        log.debug("Authentication object: {}", authentication);
        log.debug("Authentication class: {}",
                authentication != null ? authentication.getClass().getName() : "null");

        if (authentication == null) {
            return;
        }

        log.debug("Authentication name: {}", authentication.getName());
        log.debug("Authentication principal: {}", authentication.getPrincipal());
        log.debug("Authentication principal class: {}",
                authentication.getPrincipal() != null
                        ? authentication.getPrincipal().getClass().getName()
                        : "null");
        log.debug("Authentication credentials: {}", authentication.getCredentials());
        log.debug("Authentication authorities: {}", authentication.getAuthorities());
        log.debug("Authentication details: {}", authentication.getDetails());
        log.debug("Authentication authenticated flag: {}", authentication.isAuthenticated());

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            log.debug("OAuth2 user attributes: {}", oauth2User.getAttributes());
            log.debug("OAuth2 user name: {}", oauth2User.getName());
        }
    }
}
