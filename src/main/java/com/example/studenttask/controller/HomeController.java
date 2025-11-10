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
import java.util.Set;
import com.example.studenttask.model.Group;
import java.util.List;
import com.example.studenttask.service.GroupService;


@Controller
public class HomeController {

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
        System.out.println("🔧 === DEBUG: Debug Controller START ===");

        if (token == null) {
            System.out.println("❌ No OAuth2 token found");
            return "redirect:/login";
        }

        OAuth2User principal = token.getPrincipal();

        // Benutzerinformationen aus OAuth2
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");

        System.out.println("📧 OAuth2 User: " + name + " (" + email + ")");

        // User aus Datenbank laden
        String openIdSubject = principal.getAttribute("sub");
        User user = userService.findUserByOpenIdSubject(openIdSubject);

        if (user != null) {
            System.out.println("✅ User found in database: " + user.getId());

            // Rollen und Gruppen laden
            Set<Role> roles = user.getRoles();
            // Get groups from GroupService
            System.out.println("🔍 Fetching groups for user...");
            List<Group> groups = groupService.getGroupsForUser(user);
            System.out.println("🔍 Groups fetched. Count: " + (groups != null ? groups.size() : "null"));


            // Role-basierte Flags setzen
            boolean isTeacher = roles.stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER"));
            boolean isStudent = roles.stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));

            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("groups", groups);
            model.addAttribute("isTeacher", isTeacher);
            model.addAttribute("isStudent", isStudent);

            System.out.println("🎭 Role evaluation: isTeacher=" + isTeacher + ", isStudent=" + isStudent);
            System.out.println("👥 Groups assigned to user:");
            if (groups != null && !groups.isEmpty()) {
                for (Group group : groups) {
                    System.out.println(" - Group ID: " + group.getId() + ", Name: '" + group.getName() + "'");
                }
            } else {
                System.out.println(" - No groups assigned");
            }
        } else {
            System.out.println("❌ User not found in database");
            model.addAttribute("user", null);
        }

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("attributes", principal.getAttributes());

        System.out.println("🔧 === DEBUG: Debug Controller END ===");
        return "debug";
    }
}