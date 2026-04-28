package com.example.studenttask.service;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Check if user is in a specific group
     */
    public boolean isInGroup(User user, String groupName) {
        return user.getGroups().stream()
                .anyMatch(group -> group.getName().equals(groupName));
    }

    /**
     * Add user to group
     */
    public void addUserToGroup(User user, Group group) {
        user.getGroups().add(group);
        group.getUsers().add(user);
        userRepository.save(user);
    }

    /**
     * Remove user from group
     */
    public void removeUserFromGroup(User user, Group group) {
        user.getGroups().remove(group);
        group.getUsers().remove(user);
        userRepository.save(user);
    }

    /**
     * Get all teachers
     */
    public List<User> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(this::hasTeacherRole)
                .collect(Collectors.toList());
    }

    /**
     * Get all students
     */
    public List<User> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(this::hasStudentRole)
                .collect(Collectors.toList());
    }

    /**
     * Get users by group
     */
    public List<User> getUsersByGroupName(String groupName) {
        return userRepository.findByGroupName(groupName);
    }

    /**
     * Find user by OpenID Connect subject
     */
    public Optional<User> findByOpenIdSubject(String openIdSubject) {
        return userRepository.findByOpenIdSubject(openIdSubject);
    }

    /**
     * Überprüft, ob ein Benutzer (identifiziert durch openIdSubject) eine Lehrerrolle hat
     */
    public boolean hasTeacherRole(String openIdSubject) {
        return findByOpenIdSubject(openIdSubject)
                .map(this::hasTeacherRole)
                .orElse(false);
    }

    public boolean hasTeacherRole(User user) {
        return hasRoleMatching(user, this::matchesTeacherRoleName);
    }

    public boolean hasStudentRole(User user) {
        return hasRoleMatching(user, this::matchesStudentRoleName);
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(User user, String roleName) {
        return user != null
                && user.getRoles() != null
                && user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByPreferredUsername(String preferredUsername) {
        return userRepository.findByPreferredUsername(preferredUsername);
    }

    private boolean hasRoleMatching(User user, java.util.function.Predicate<String> matcher) {
        return user != null
                && user.getRoles() != null
                && user.getRoles().stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .anyMatch(matcher);
    }

    private boolean matchesTeacherRoleName(String roleName) {
        String normalizedRoleName = roleName.toLowerCase(Locale.ROOT);
        return "role_teacher".equals(normalizedRoleName)
                || "teacher".equals(normalizedRoleName)
                || "lehrer".equals(normalizedRoleName)
                || normalizedRoleName.contains("teacher")
                || normalizedRoleName.contains("lehrer");
    }

    private boolean matchesStudentRoleName(String roleName) {
        String normalizedRoleName = roleName.toLowerCase(Locale.ROOT);
        return "role_student".equals(normalizedRoleName)
                || "student".equals(normalizedRoleName)
                || "schueler".equals(normalizedRoleName)
                || "schüler".equals(normalizedRoleName)
                || normalizedRoleName.contains("student")
                || normalizedRoleName.contains("schueler")
                || normalizedRoleName.contains("schüler");
    }
}
