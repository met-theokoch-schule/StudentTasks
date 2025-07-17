
package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

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
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model, HttpSession session) {
        System.out.println("=== DASHBOARD CONTROLLER CALLED ===");
        
        if (principal != null) {
            System.out.println("OAuth2User: " + principal.getName());
            System.out.println("Attributes: " + principal.getAttributes());
            
            // Create or get user directly here
            User user = createOrGetUser(principal);
            
            // Store user in session
            session.setAttribute("user", user);
            
            model.addAttribute("user", user);
            model.addAttribute("oauth2User", principal);
            model.addAttribute("attributes", principal.getAttributes());
            
            System.out.println("User object created/retrieved: " + (user != null ? user.getName() : "NULL"));
        }
        
        return "dashboard";
    }

    private User createOrGetUser(OAuth2User oauth2User) {
        System.out.println("=== CREATING/GETTING USER ===");
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        String subject = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String preferredUsername = oauth2User.getAttribute("preferred_username");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        
        System.out.println("Subject: " + subject);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        
        // Extract role names from OAuth2 attributes
        Set<String> roleNames = new HashSet<>();
        Object rolesObj = attributes.get("roles");
        if (rolesObj instanceof List<?>) {
            for (Object roleObj : (List<?>) rolesObj) {
                if (roleObj instanceof Map<?, ?>) {
                    Map<?, ?> roleMap = (Map<?, ?>) roleObj;
                    String roleId = (String) roleMap.get("id");
                    if (roleId != null) {
                        String roleName = roleId.startsWith("ROLE_") ? roleId.substring(5) : roleId;
                        roleNames.add(roleName);
                        System.out.println("Extracted role: " + roleName);
                    }
                }
            }
        }

        // Extract group names from OAuth2 attributes
        Set<String> groupNames = new HashSet<>();
        Object groupsObj = attributes.get("groups");
        if (groupsObj instanceof Map<?, ?>) {
            Map<?, ?> groupsMap = (Map<?, ?>) groupsObj;
            for (Object groupObj : groupsMap.values()) {
                if (groupObj instanceof Map<?, ?>) {
                    Map<?, ?> groupMap = (Map<?, ?>) groupObj;
                    String groupName = (String) groupMap.get("name");
                    if (groupName != null) {
                        groupNames.add(groupName);
                        System.out.println("Extracted group: " + groupName);
                    }
                }
            }
        }

        // Create or update user using UserService
        User user = userService.createOrUpdateUser(
            subject, 
            name != null ? name : preferredUsername, 
            email, 
            preferredUsername,
            givenName, 
            familyName, 
            roleNames, 
            groupNames
        );
        
        System.out.println("User service returned: " + (user != null ? user.getName() : "NULL"));
        System.out.println("=== END USER CREATION ===");
        
        return user;
    }
}
