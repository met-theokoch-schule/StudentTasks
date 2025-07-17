package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import com.example.studenttask.model.Role;

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
    public String dashboard(Model model, Authentication authentication) {
        System.out.println("🌐 === DEBUG: Dashboard Controller START ===");

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Token.getPrincipal();

        System.out.println("🔑 OAuth2 User Principal loaded:");
        System.out.println("   - Principal name: " + oauth2User.getName());
        System.out.println("   - All attributes: " + oauth2User.getAttributes());

        // Get user from database
        String openIdSubject = oauth2User.getAttribute("sub");
        System.out.println("🔍 Looking for user with OpenID Subject: " + openIdSubject);

        User user = userService.findByOpenIdSubject(openIdSubject);

        if (user == null) {
            System.out.println("❌ User NOT found in database - triggering creation process");
            // Create user if not exists
            user = userService.createOrUpdateUserFromOAuth2(oauth2User);
            System.out.println("✅ User creation process completed, user ID: " + (user != null ? user.getId() : "NULL"));
        } else {
            System.out.println("✅ User FOUND in database:");
            System.out.println("   - User ID: " + user.getId());
            System.out.println("   - Name: " + user.getName());
            System.out.println("   - Email: " + user.getEmail());
            System.out.println("   - Roles: " + (user.getRoles() != null ? user.getRoles().size() : 0));
            System.out.println("   - Groups: " + (user.getGroups() != null ? user.getGroups().size() : 0));
        }

        // Set model attributes with detailed logging
        System.out.println("📝 Setting model attributes...");
        model.addAttribute("user", user);
        model.addAttribute("roles", user != null ? user.getRoles() : null);
        model.addAttribute("groups", user != null ? user.getGroups() : null);
        model.addAttribute("name", oauth2User.getAttribute("name"));
        model.addAttribute("email", oauth2User.getAttribute("email"));
        model.addAttribute("attributes", oauth2User.getAttributes());

        // Role-based flags
        boolean isTeacher = user != null && user.getRoles().stream()
            .anyMatch(role -> "TEACHER".equals(role.getName()));
        boolean isStudent = user != null && user.getRoles().stream()
            .anyMatch(role -> "STUDENT".equals(role.getName()));

        System.out.println("🎭 Role evaluation:");
        System.out.println("   - Is Teacher: " + isTeacher);
        System.out.println("   - Is Student: " + isStudent);
        System.out.println("   - Available roles: " + (user != null && user.getRoles() != null ? 
            user.getRoles().stream().map(Role::getName).toArray() : "NONE"));

        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);

        System.out.println("🌐 === DEBUG: Dashboard Controller END ===");
        return "dashboard";
    }
}