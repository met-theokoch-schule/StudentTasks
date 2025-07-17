
package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

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
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model, HttpSession session) {
        if (principal != null) {
            // Try to get user from session first
            User user = (User) session.getAttribute("user");
            
            System.out.println("=== DASHBOARD DEBUG ===");
            System.out.println("OAuth2User: " + principal.getName());
            System.out.println("User from session: " + (user != null ? user.getName() : "NULL"));
            System.out.println("=======================");
            
            model.addAttribute("user", user);
            
            if (user != null) {
                if (user.isTeacher()) {
                    return "redirect:/teacher/dashboard";
                } else if (user.isStudent()) {
                    return "redirect:/student/dashboard";
                }
            } else {
                // Fallback: add OAuth2User attributes for debugging
                model.addAttribute("oauth2User", principal);
                model.addAttribute("attributes", principal.getAttributes());
            }
        }
        return "dashboard";
    }
}
