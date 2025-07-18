
package com.example.studenttask.service;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    /**
     * Get current authenticated user
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String openIdSubject = oauth2User.getAttribute("sub");
            if (openIdSubject != null) {
                return userService.findByOpenIdSubject(openIdSubject);
            }
        }
        return Optional.empty();
    }

    /**
     * Get current OAuth2 user
     */
    public Optional<OAuth2User> getCurrentOAuth2User() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return Optional.of((OAuth2User) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    /**
     * Check if current user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               authentication.getPrincipal() instanceof OAuth2User;
    }

    /**
     * Check if current user has a specific role
     */
    public boolean hasRole(String roleName) {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(user -> userService.hasRole(user, roleName)).orElse(false);
    }

    /**
     * Check if current user is a teacher
     */
    public boolean isTeacher() {
        return hasRole("TEACHER");
    }

    /**
     * Check if current user is a student
     */
    public boolean isStudent() {
        return hasRole("STUDENT");
    }

    /**
     * Check if current user is an admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is in a specific group
     */
    public boolean isInGroup(String groupName) {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(user -> userService.isInGroup(user, groupName)).orElse(false);
    }

    /**
     * Get current user's roles
     */
    public Set<Role> getCurrentUserRoles() {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(User::getRoles).orElse(new HashSet<>());
    }

    /**
     * Get current user's groups
     */
    public Set<Group> getCurrentUserGroups() {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(User::getGroups).orElse(new HashSet<>());
    }

    /**
     * Get current user's display name
     */
    public String getCurrentUserDisplayName() {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(User::getName).orElse("Unknown User");
    }

    /**
     * Get current user's email
     */
    public String getCurrentUserEmail() {
        Optional<User> userOpt = getCurrentUser();
        return userOpt.map(User::getEmail).orElse("");
    }

    /**
     * Logout current user
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
