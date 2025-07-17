
package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginTestController {

    @GetMapping("/test/login")
    public String loginTest(@AuthenticationPrincipal OAuth2User principal, Model model, HttpSession session) {
        if (principal != null) {
            model.addAttribute("oauth2User", principal);
            model.addAttribute("attributes", principal.getAttributes());
            model.addAttribute("authorities", principal.getAuthorities());
            
            // Get user from session
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            
            // Debug-Info hinzufügen
            System.out.println("=== LOGIN TEST DEBUG ===");
            System.out.println("OAuth2User: " + principal.getName());
            System.out.println("Authorities: " + principal.getAuthorities());
            System.out.println("User object from session: " + (user != null ? user.getName() : "NULL"));
            System.out.println("=========================");
        }
        return "test/login-test";
    }
    
    @GetMapping("/test/force-logout")
    public String forceLogout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }
}
