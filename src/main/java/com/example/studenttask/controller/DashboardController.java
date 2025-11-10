package com.example.studenttask.controller;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.Group;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

import java.util.List;
import java.util.Set;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService; // Add this line for GroupService injection

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        System.out.println("🌐 === DEBUG: Dashboard Controller START ===");

        // Benutzer aus der Datenbank laden
        String openIdSubject = authentication.getName();
        System.out.println("🔍 Looking for user with OpenID Subject: " + openIdSubject);

        User user = userService.findUserByOpenIdSubject(openIdSubject);
        if (user == null) {
            System.out.println("❌ User not found, attempting to create from OAuth2 data");

            // Versuche Benutzer aus OAuth2-Daten zu erstellen
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                OAuth2User oauth2User = oauth2Token.getPrincipal();
                
                System.out.println("==========================================");
                System.out.println("🔍 COMPLETE OAUTH2 TOKEN DUMP (DashboardController):");
                System.out.println("==========================================");
                Map<String, Object> allAttributes = oauth2User.getAttributes();
                for (Map.Entry<String, Object> entry : allAttributes.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println("   [" + key + "] = " + value);
                    System.out.println("      Type: " + (value != null ? value.getClass().getName() : "null"));
                    if (value != null && value.getClass().isArray()) {
                        System.out.println("      Array content: " + java.util.Arrays.toString((Object[]) value));
                    } else if (value instanceof java.util.List) {
                        System.out.println("      List content: " + value);
                    } else if (value instanceof java.util.Map) {
                        System.out.println("      Map content: " + value);
                    }
                }
                System.out.println("==========================================");
                
                user = userService.createOrUpdateUserFromOAuth2(oauth2User);
                System.out.println("✅ User created with ID: " + (user != null ? user.getId() : "NULL"));
            }

            if (user == null) {
                System.out.println("❌ User creation failed, redirecting to login");
                return "redirect:/login";
            }
        }

        System.out.println("✅ User found: " + user.getName() + " (ID: " + user.getId() + ")");

        // Role-based flags
        System.out.println("🎭 Role evaluation started...");
        System.out.println("   - User roles count: " + (user.getRoles() != null ? user.getRoles().size() : 0));

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                System.out.println("   - Found role: '" + role.getName() + "'");
            }
        }

        boolean isTeacher = user.getRoles() != null && user.getRoles().stream()
            .anyMatch(role -> {
                String roleName = role.getName();
                System.out.println("   - Checking role: '" + roleName + "' for teacher match");
                // Check various teacher role patterns
                return "ROLE_TEACHER".equals(roleName) ||
                       "teacher".equals(roleName) ||
                       "lehrer".equals(roleName) ||
                       roleName.toLowerCase().contains("teacher") ||
                       roleName.toLowerCase().contains("lehrer");
            });

        boolean isStudent = user.getRoles() != null && user.getRoles().stream()
            .anyMatch(role -> {
                String roleName = role.getName();
                System.out.println("   - Checking role: '" + roleName + "' for student match");
                // Check various student role patterns
                return "ROLE_STUDENT".equals(roleName) ||
                       "student".equals(roleName) ||
                       "schueler".equals(roleName) ||
                       "schüler".equals(roleName) ||
                       roleName.toLowerCase().contains("student") ||
                       roleName.toLowerCase().contains("schueler") ||
                       roleName.toLowerCase().contains("schüler");
            });

        System.out.println("🎭 Role evaluation results:");
        System.out.println("   - Is Teacher: " + isTeacher);
        System.out.println("   - Is Student: " + isStudent);

        // Gruppen laden und ausgeben
        System.out.println("==========================================");
        System.out.println("🔍 DEBUG: About to fetch groups for user ID: " + user.getId() + ", Name: " + user.getName());

        List<Group> groups = null;
        try {
            groups = groupService.getGroupsForUser(user);
            System.out.println("🔍 DEBUG: Groups fetched successfully!");
            System.out.println("🔍 DEBUG: Groups result: " + (groups == null ? "NULL" : groups.size() + " groups"));

            System.out.println("👥 Groups assigned to user:");
            if (groups != null && !groups.isEmpty()) {
                for (Group group : groups) {
                    System.out.println("   - Group ID: " + group.getId() + ", Name: '" + group.getName() + "'");
                }
            } else {
                System.out.println("   - No groups assigned (or NULL)");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR: Exception while fetching groups!");
            System.out.println("❌ Exception message: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==========================================");

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
}