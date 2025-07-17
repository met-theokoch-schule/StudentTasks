
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

import java.util.HashSet;
import java.util.List;
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

    public User createOrUpdateUser(String subject, String email, String name, 
                                 String preferredUsername, String givenName, 
                                 String familyName, List<String> roleNames, 
                                 List<String> groupNames) {

        System.out.println("=== UserService.createOrUpdateUser called ===");
        System.out.println("Subject: " + subject);
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Role names: " + roleNames);
        System.out.println("Group names: " + groupNames);

        try {
            // Find existing user or create new one
            Optional<User> existingUser = userRepository.findBySubject(subject);
            User user;
            
            if (existingUser.isPresent()) {
                user = existingUser.get();
                System.out.println("Found existing user: " + user.getName());
            } else {
                user = new User();
                user.setSubject(subject);
                System.out.println("Creating new user");
            }

            // Update user properties
            user.setEmail(email);
            user.setName(name);
            user.setPreferredUsername(preferredUsername);
            user.setGivenName(givenName);
            user.setFamilyName(familyName);

            // Handle roles
            Set<Role> userRoles = new HashSet<>();
            if (roleNames != null && !roleNames.isEmpty()) {
                for (String roleName : roleNames) {
                    try {
                        Optional<Role> roleOpt = roleRepository.findByName(roleName);
                        if (roleOpt.isPresent()) {
                            userRoles.add(roleOpt.get());
                            System.out.println("Added role: " + roleName);
                        } else {
                            System.out.println("Role not found: " + roleName);
                        }
                    } catch (Exception e) {
                        System.err.println("Error finding role " + roleName + ": " + e.getMessage());
                    }
                }
            }

            // If no roles found, assign default STUDENT role
            if (userRoles.isEmpty()) {
                try {
                    Optional<Role> studentRoleOpt = roleRepository.findByName("STUDENT");
                    if (studentRoleOpt.isPresent()) {
                        userRoles.add(studentRoleOpt.get());
                        System.out.println("Assigned default STUDENT role");
                    } else {
                        System.err.println("Default STUDENT role not found!");
                    }
                } catch (Exception e) {
                    System.err.println("Error assigning default role: " + e.getMessage());
                }
            }

            user.setRoles(userRoles);

            // Handle groups
            Set<Group> userGroups = new HashSet<>();
            if (groupNames != null && !groupNames.isEmpty()) {
                for (String groupName : groupNames) {
                    try {
                        Optional<Group> groupOpt = groupRepository.findByName(groupName);
                        if (groupOpt.isPresent()) {
                            userGroups.add(groupOpt.get());
                            System.out.println("Added group: " + groupName);
                        } else {
                            System.out.println("Group not found: " + groupName);
                        }
                    } catch (Exception e) {
                        System.err.println("Error finding group " + groupName + ": " + e.getMessage());
                    }
                }
            }
            user.setGroups(userGroups);

            // Save user
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully: " + savedUser.getName());
            System.out.println("=== UserService.createOrUpdateUser finished ===");
            
            return savedUser;

        } catch (Exception e) {
            System.err.println("Error in createOrUpdateUser: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create or update user", e);
        }
    }

    public Optional<User> findBySubject(String subject) {
        return userRepository.findBySubject(subject);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
