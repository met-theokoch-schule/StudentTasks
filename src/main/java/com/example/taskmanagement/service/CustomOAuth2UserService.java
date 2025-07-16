package com.example.taskmanagement.service;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
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

        User user = null;
        try {
            // Create or update user
            user = userService.createOrUpdateUser(subject, email, name, preferredUsername, givenName, familyName, attributes);
            System.out.println("User created/updated successfully: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error creating/updating user: " + e.getMessage());
            e.printStackTrace();
        }

        // Extract authorities
        List<String> authorities = new ArrayList<>();
        authorities.add("OIDC_USER");

        // Extract roles from OAuth2 attributes
        Object rolesObj = attributes.get("roles");
        if (rolesObj instanceof List<?>) {
            for (Object roleObj : (List<?>) rolesObj) {
                if (roleObj instanceof Map<?, ?>) {
                    Map<?, ?> roleMap = (Map<?, ?>) roleObj;
                    String roleId = (String) roleMap.get("id");
                    if (roleId != null) {
                        // Extrahiere nur den Rollennamen (z.B. "TEACHER" aus "ROLE_TEACHER")
                        String roleName = roleId.startsWith("ROLE_") ? roleId.substring(5) : roleId;
                        authorities.add("ROLE_" + roleName);
                        System.out.println("Added role authority: ROLE_" + roleName);
                    }
                }
            }
        }

        // Extract group names from attributes (verwende "act" als Gruppennamen)
        Object groupsObj = attributes.get("groups");
        if (groupsObj instanceof Map<?, ?>) {
            Map<?, ?> groupsMap = (Map<?, ?>) groupsObj;
            for (Object groupObj : groupsMap.values()) {
                if (groupObj instanceof Map<?, ?>) {
                    Map<?, ?> groupMap = (Map<?, ?>) groupObj;
                    String actName = (String) groupMap.get("act");
                    if (actName != null) {
                        authorities.add("GROUP_" + actName);
                        System.out.println("Added group authority: GROUP_" + actName);
                    }
                }
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