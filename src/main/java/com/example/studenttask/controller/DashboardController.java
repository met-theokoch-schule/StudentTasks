
package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Role;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        System.out.println("🌐 === DEBUG: Dashboard Controller START ===");

        // Benutzer aus der Datenbank laden
        String openIdSubject = authentication.getName();
        System.out.println("🔍 Looking for user with OpenID Subject: " + openIdSubject);

        User user = userService.findUserByOpenIdSubject(openIdSubject);
        if (user == null) {
            System.out.println("❌ User not found, redirecting to login");
            return "redirect:/login";
        }

        System.out.println("✅ User found: " + user.getName() + " (ID: " + user.getId() + ")");

        // Überprüfe Rollen und leite entsprechend weiter
        boolean isTeacher = false;
        boolean isStudent = false;

        for (Role role : user.getRoles()) {
            if ("TEACHER".equals(role.getName())) {
                isTeacher = true;
            }
            if ("STUDENT".equals(role.getName())) {
                isStudent = true;
            }
        }

        System.out.println("🎭 Role evaluation:");
        System.out.println("   - Is Teacher: " + isTeacher);
        System.out.println("   - Is Student: " + isStudent);
        System.out.println("   - Available roles: " + user.getRoles().toArray());

        if (isTeacher) {
            return "redirect:/teacher/dashboard";
        } else if (isStudent) {
            return "redirect:/student/dashboard";
        } else {
            // Für alle anderen zeige das allgemeine Dashboard
            return "dashboard";
        }
    }
}
