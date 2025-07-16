
package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

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
            User user = (User) principal.getAttribute("user");
            model.addAttribute("user", user);
            
            if (user.isTeacher()) {
                return "redirect:/teacher/dashboard";
            } else if (user.isStudent()) {
                return "redirect:/student/dashboard";
            }
        }
        return "dashboard";
    }
}
