package com.example.taskmanagement.service;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.Group;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        return processOAuth2User(oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        String subject = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String preferredUsername = oauth2User.getAttribute("preferred_username");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");

        System.out.println("=== OAuth2 User Processing ===");
        System.out.println("Subject: " + subject);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Preferred Username: " + preferredUsername);
        System.out.println("Given Name: " + givenName);
        System.out.println("Family Name: " + familyName);
        System.out.println("Groups from 'groups' attribute: " + attributes.get("groups"));
        System.out.println("Roles from 'roles' attribute: " + attributes.get("roles"));

        // Extract role names from OAuth2 attributes
        Set<String> roleNames = new HashSet<>();
        Object rolesObj = attributes.get("roles");
        if (rolesObj instanceof List<?>) {
            for (Object roleObj : (List<?>) rolesObj) {
                if (roleObj instanceof Map<?, ?>) {
                    Map<?, ?> roleMap = (Map<?, ?>) roleObj;
                    String roleId = (String) roleMap.get("id");
                    if (roleId != null) {
                        // Extract role name (e.g., "TEACHER" from "ROLE_TEACHER")
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
                    String actName = (String) groupMap.get("act");
                    if (actName != null) {
                        groupNames.add(actName);
                        System.out.println("Extracted group: " + actName);
                    }
                }
            }
        }

        User user = null;
        try {
            // Create or update user with extracted role and group names
            user = userService.createOrUpdateUser(subject, name, email, preferredUsername, givenName, familyName, roleNames, groupNames);
            System.out.println("User created/updated successfully: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error creating/updating user: " + e.getMessage());
            e.printStackTrace();
        }

        // Extract authorities from the created user
        List<String> authorities = new ArrayList<>();
        authorities.add("OIDC_USER");

        // Add role authorities
        if (user != null && user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                authorities.add("ROLE_" + role.getName());
                System.out.println("Added role authority: ROLE_" + role.getName());
            }
        }

        // Add group authorities
        if (user != null && user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                authorities.add("GROUP_" + group.getName());
                System.out.println("Added group authority: GROUP_" + group.getName());
            }
        }

        // Create custom attributes including our user entity
        if (user != null) {
            attributes.put("user", user);
            System.out.println("User added to attributes: " + user.getName());
        } else {
            System.err.println("WARNING: User object is null, not adding to attributes");
        }

        System.out.println("=== End OAuth2 User Processing ===");

        return new DefaultOAuth2User(
            authorities.stream().map(auth -> (org.springframework.security.core.GrantedAuthority) () -> auth).collect(Collectors.toList()),
            attributes,
            "sub"
        );
    }


}