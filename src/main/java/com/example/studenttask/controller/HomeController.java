package com.example.studenttask.controller;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/debug")
    public String debug(Model model, OAuth2AuthenticationToken token) {
        log.debug("Debug controller invoked");

        if (token == null) {
            log.warn("Debug page called without OAuth2 token");
            return "redirect:/login";
        }

        OAuth2User principal = token.getPrincipal();
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");

        log.debug("OAuth2 user loaded for debug page: name={}, email={}", name, email);

        String openIdSubject = principal.getAttribute("sub");
        User user = userService.findUserByOpenIdSubject(openIdSubject);

        if (user != null) {
            log.debug("User {} found in database for debug page", user.getId());

            Set<Role> roles = user.getRoles();
            List<Group> groups = null;
            try {
                groups = groupService.getGroupsForUser(user);
                log.debug("Fetched {} group(s) for debug page", groups == null ? 0 : groups.size());
            } catch (Exception e) {
                log.error("Failed to load groups for debug page and user {}", user.getId(), e);
            }

            if (log.isDebugEnabled()) {
                if (groups != null && !groups.isEmpty()) {
                    for (Group group : groups) {
                        log.debug("Group for user {}: id={}, name='{}'", user.getId(), group.getId(), group.getName());
                    }
                } else {
                    log.debug("No groups assigned to user {}", user.getId());
                }
            }

            boolean isTeacher = roles.stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER"));
            boolean isStudent = roles.stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));

            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("isTeacher", isTeacher);
            model.addAttribute("isStudent", isStudent);

            log.debug("Role evaluation for user {}: isTeacher={}, isStudent={}", user.getId(), isTeacher, isStudent);
        } else {
            log.warn("User with subject {} not found in database for debug page", openIdSubject);
            model.addAttribute("user", null);
        }

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("attributes", principal.getAttributes());

        log.debug("Debug controller finished");
        return "debug";
    }
}
