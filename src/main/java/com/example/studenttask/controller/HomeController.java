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

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String openIdSubject = principal.getAttribute("sub");
            User user = userService.findByOpenIdSubject(openIdSubject);

            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("attributes", principal.getAttributes());
            model.addAttribute("user", user);

            if (user != null) {
                model.addAttribute("roles", user.getRoles());
                model.addAttribute("groups", user.getGroups());
                model.addAttribute("isTeacher", user.isTeacher());
                model.addAttribute("isStudent", user.isStudent());
            }
        }
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}