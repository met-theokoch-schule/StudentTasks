
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
}
