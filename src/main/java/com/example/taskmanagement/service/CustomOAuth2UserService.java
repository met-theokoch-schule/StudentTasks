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
        Map<String, Object> attributes = oauth2User.getAttributes();
        String subject = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        System.out.println("OAuth2 Attributes: " + attributes);
        System.out.println("Subject: " + subject);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Groups from 'groups' attribute: " + attributes.get("groups"));
        System.out.println("Roles from 'roles' attribute: " + attributes.get("roles"));

        User user = userRepository.findByOpenIdSubject(subject)
            .orElseGet(() -> {
                User newUser = new User(subject, name, email);
                return userService.createUser(newUser);
            });

        // Update user info if changed
        if (!user.getName().equals(name) || !user.getEmail().equals(email)) {
            user.setName(name);
            user.setEmail(email);
            userRepository.save(user);
        }

        // Build authorities from user roles and groups
        Collection<String> authorities = new ArrayList<>();

        // Extract group names from attributes
        List<String> groupNames = extractListFromAttribute(attributes, "groups");
        if (groupNames != null) {
            groupNames.forEach(groupName -> authorities.add("GROUP_" + groupName));
        }

        user.getRoles().forEach(role -> authorities.add("ROLE_" + role.getName()));

        // Create custom attributes including our user entity
        attributes.put("user", user);

        return new DefaultOAuth2User(
            authorities.stream().map(auth -> (org.springframework.security.core.GrantedAuthority) () -> auth).collect(Collectors.toList()),
            attributes,
            "sub"
        );
    }

    @SuppressWarnings("unchecked")
    private List<String> extractListFromAttribute(Map<String, Object> attributes, String attributeName) {
        Object attributeValue = attributes.get(attributeName);
        if (attributeValue instanceof List<?>) {
            return ((List<?>) attributeValue).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        return null;
    }
}