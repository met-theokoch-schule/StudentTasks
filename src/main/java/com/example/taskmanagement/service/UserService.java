package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Group;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.GroupRepository;
import com.example.taskmanagement.repository.RoleRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    public User createUser(User user) {
        // Assign default STUDENT role if no roles assigned
        if (user.getRoles().isEmpty()) {
            Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Default STUDENT role not found"));
            user.getRoles().add(studentRole);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByOpenIdSubject(String subject) {
        return userRepository.findByOpenIdSubject(subject);
    }

    public User addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User addGroupToUser(Long userId, String groupName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository.findByName(groupName)
            .orElseThrow(() -> new RuntimeException("Group not found"));

        user.getGroups().add(group);
        return userRepository.save(user);
    }

    public User createOrUpdateUser(String subject, String name, String email,
                                  String preferredUsername, String givenName, String familyName,
                                  Set<String> roleNames, Set<String> groupNames) {
        // Try to find existing user
        Optional<User> existingUser = findByOpenIdSubject(subject);
        User user;

        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            user.setName(name);
            user.setEmail(email);
            user.setPreferredUsername(preferredUsername);
            user.setGivenName(givenName);
            user.setFamilyName(familyName);

            System.out.println("Updating existing user: " + user.getName());
        } else {
            // Create new user
            user = new User(subject, name, email, preferredUsername, givenName, familyName);
            System.out.println("Creating new user: " + user.getName());
        }

        // Clear existing roles and groups
        user.getRoles().clear();
        user.getGroups().clear();

        // Set roles
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Optional<Role> roleOpt = roleRepository.findByName(roleName);
                if (roleOpt.isPresent()) {
                    user.getRoles().add(roleOpt.get());
                    System.out.println("Added role: " + roleName);
                } else {
                    // Create role if it doesn't exist
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription("Auto-created role for " + roleName);
                    Role savedRole = roleRepository.save(newRole);
                    user.getRoles().add(savedRole);
                    System.out.println("Created and added new role: " + roleName);
                }
            }
        }

        // Set groups
        if (groupNames != null && !groupNames.isEmpty()) {
            for (String groupName : groupNames) {
                Optional<Group> groupOpt = groupRepository.findByName(groupName);
                if (groupOpt.isPresent()) {
                    user.getGroups().add(groupOpt.get());
                    System.out.println("Added group: " + groupName);
                } else {
                    // Create group if it doesn't exist
                    Group newGroup = new Group();
                    newGroup.setName(groupName);
                    newGroup.setDescription("Auto-created group for " + groupName);
                    Group savedGroup = groupRepository.save(newGroup);
                    user.getGroups().add(savedGroup);
                    System.out.println("Created and added new group: " + groupName);
                }
            }
        }

        // Assign default STUDENT role if no roles assigned
        if (user.getRoles().isEmpty()) {
            Optional<Role> studentRoleOpt = roleRepository.findByName("STUDENT");
            if (studentRoleOpt.isPresent()) {
                user.getRoles().add(studentRoleOpt.get());
                System.out.println("Added default STUDENT role");
            }
        }

        return userRepository.save(user);
    }
}