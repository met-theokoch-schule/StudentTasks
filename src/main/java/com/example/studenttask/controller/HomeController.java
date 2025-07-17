package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            // OAuth2 Daten für das Template
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("attributes", principal.getAttributes());

            // User Entity aus der Datenbank laden
            String openIdSubject = principal.getAttribute("sub");
            if (openIdSubject != null) {
                User user = userService.findUserByOpenIdSubject(openIdSubject);
                model.addAttribute("user", user);

                if (user != null) {
                    model.addAttribute("userRoles", user.getRoles());
                    model.addAttribute("userGroups", user.getGroups());
                }
            }
        }
        return "dashboard";
    }
}