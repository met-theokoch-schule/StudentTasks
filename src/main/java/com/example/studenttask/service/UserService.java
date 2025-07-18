package com.example.studenttask.service;

import com.example.studenttask.model.User;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.Group;
import com.example.studenttask.repository.UserRepository;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
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

    public User findOrCreateUserFromOAuth2(OAuth2User oauth2User) {
        String openIdSubject = oauth2User.getAttribute("sub");

        return userRepository.findByOpenIdSubject(openIdSubject)
            .map(user -> updateUserFromOAuth2(user, oauth2User))
            .orElseGet(() -> createUserFromOAuth2(oauth2User));
    }

    private User createUserFromOAuth2(OAuth2User oauth2User) {
        User user = new User();
        mapOAuth2AttributesToUser(user, oauth2User);
        return userRepository.save(user);
    }

    private User updateUserFromOAuth2(User user, OAuth2User oauth2User) {
        mapOAuth2AttributesToUser(user, oauth2User);
        return userRepository.save(user);
    }

    private void mapOAuth2AttributesToUser(User user, OAuth2User oauth2User) {
        // Standard Claims mapping
        user.setOpenIdSubject(oauth2User.getAttribute("sub"));
        user.setEmail(oauth2User.getAttribute("email"));
        user.setName(oauth2User.getAttribute("name"));
        user.setPreferredUsername(oauth2User.getAttribute("preferred_username"));
        user.setGivenName(oauth2User.getAttribute("given_name"));
        user.setFamilyName(oauth2User.getAttribute("family_name"));

        // Rollen aus OAuth2 Claims extrahieren
        mapRolesFromOAuth2(user, oauth2User);

        // Gruppen aus OAuth2 Claims extrahieren
        mapGroupsFromOAuth2(user, oauth2User);
    }

    @SuppressWarnings("unchecked")
    private void mapRolesFromOAuth2(User user, OAuth2User oauth2User) {
        user.getRoles().clear();

        List<Map<String, Object>> roles = oauth2User.getAttribute("roles");
        if (roles != null) {
            for (Map<String, Object> roleData : roles) {
                String roleId = (String) roleData.get("id");
                String displayName = (String) roleData.get("displayName");

                if (roleId != null) {
                    Role role = roleRepository.findByName(roleId)
                        .orElseGet(() -> {
                            Role newRole = new Role(roleId, displayName);
                            return roleRepository.save(newRole);
                        });
                    user.addRole(role);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void mapGroupsFromOAuth2(User user, OAuth2User oauth2User) {
        user.getGroups().clear();

        Map<String, Map<String, Object>> groups = oauth2User.getAttribute("groups");
        if (groups != null) {
            for (Map.Entry<String, Map<String, Object>> groupEntry : groups.entrySet()) {
                Map<String, Object> groupData = groupEntry.getValue();
                String groupName = (String) groupData.get("act");
                String groupDescription = (String) groupData.get("desc");

                if (groupName != null) {
                    Group group = groupRepository.findByName(groupName)
                        .orElseGet(() -> {
                            Group newGroup = new Group(groupName, groupDescription);
                            return groupRepository.save(newGroup);
                        });
                    user.addGroup(group);
                }
            }
        }
    }

    public User findUserByOpenIdSubject(String openIdSubject) {
        System.out.println("🔍 DEBUG: Looking for user with OpenID Subject: " + openIdSubject);
        Optional<User> userOpt = userRepository.findByOpenIdSubject(openIdSubject);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ DEBUG: User found in database: " + user.getName());
            return user;
        } else {
            System.out.println("❌ DEBUG: User NOT found in database");
            return null;
        }
    }

    public User createOrUpdateUserFromOAuth2(OAuth2User oauth2User) {
        System.out.println("🔍 === DEBUG: OAuth2 User Creation/Update Process START ===");

        // Extract OAuth2 attributes with detailed logging
        String openIdSubject = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String preferredUsername = oauth2User.getAttribute("preferred_username");

        System.out.println("📋 OAuth2 Attributes extracted:");
        System.out.println("   - OpenID Subject: " + openIdSubject);
        System.out.println("   - Name: " + name);
        System.out.println("   - Email: " + email);
        System.out.println("   - Given Name: " + givenName);
        System.out.println("   - Family Name: " + familyName);
        System.out.println("   - Preferred Username: " + preferredUsername);

        // Check if user exists
        System.out.println("🔍 Checking if user exists in database...");
        Optional<User> userOpt = userRepository.findByOpenIdSubject(openIdSubject);
        User user = null;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            System.out.println("📝 User FOUND - updating existing user with ID: " + user.getId());

        } else {
            System.out.println("✨ User NOT found - creating NEW user");
            user = new User();
            user.setOpenIdSubject(openIdSubject);
        }

        // Set basic user attributes
        System.out.println("📝 Setting user attributes...");
        user.setName(name);
        user.setEmail(email);
        user.setGivenName(givenName);
        user.setFamilyName(familyName);
        user.setPreferredUsername(preferredUsername);

        // Handle roles from OAuth2 attributes
        System.out.println("🎭 Processing roles...");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rolesList = oauth2User.getAttribute("roles");
        System.out.println("   - Roles raw data: " + rolesList);

        if (rolesList != null) {
            Set<Role> roles = new HashSet<>();
            System.out.println("   - Processing " + rolesList.size() + " role(s)");

            for (Map<String, Object> roleData : rolesList) {
                String roleId = (String) roleData.get("id");
                System.out.println("   - Processing role ID: " + roleId);

                if (roleId != null) {
                    Optional<Role> roleOpt = roleRepository.findByName(roleId);
                    Role role = null;

                    if (roleOpt.isPresent()) {
                        role = roleOpt.get();
                    }

                    if (role == null) {
                        System.out.println("   - Creating NEW role: " + roleId);
                        role = new Role();
                        role.setName(roleId);
                        role.setDescription("Role from OAuth2: " + roleId);
                        role = roleRepository.save(role);
                        System.out.println("   - Role saved with ID: " + role.getId());
                    } else {
                        System.out.println("   - Using existing role: " + role.getName() + " (ID: " + role.getId() + ")");
                    }
                    roles.add(role);
                }
            }
            user.setRoles(roles);
            System.out.println("   - Total roles assigned: " + roles.size());
        } else {
            System.out.println("   - No roles data received from OAuth2");
        }

        // Handle groups from OAuth2 attributes
        System.out.println("👥 Processing groups...");
        @SuppressWarnings("unchecked")
        Map<String, Object> groupsData = oauth2User.getAttribute("groups");
        System.out.println("   - Groups raw data: " + groupsData);

        if (groupsData != null) {
            Set<Group> groups = new HashSet<>();
            System.out.println("   - Processing " + groupsData.size() + " group(s)");

            for (Map.Entry<String, Object> entry : groupsData.entrySet()) {
                System.out.println("   - Processing group key: " + entry.getKey());
                @SuppressWarnings("unchecked")
                Map<String, Object> groupInfo = (Map<String, Object>) entry.getValue();
                String groupName = (String) groupInfo.get("act");
                System.out.println("   - Group name (act): " + groupName);

                if (groupName != null) {
                    Optional<Group> groupOpt = groupRepository.findByName(groupName);
                    Group group = null;

                    if (groupOpt.isPresent()) {
                        group = groupOpt.get();
                    }
                    if (group == null) {
                        System.out.println("   - Creating NEW group: " + groupName);
                        group = new Group();
                        group.setName(groupName);
                        group.setDescription("Group from OAuth2: " + groupName);
                        group = groupRepository.save(group);
                        System.out.println("   - Group saved with ID: " + group.getId());
                    } else {
                        System.out.println("   - Using existing group: " + group.getName() + " (ID: " + group.getId() + ")");
                    }
                    groups.add(group);
                }
            }
            user.setGroups(groups);
            System.out.println("   - Total groups assigned: " + groups.size());
        } else {
            System.out.println("   - No groups data received from OAuth2");
        }

        // Save user to database
        System.out.println("💾 Saving user to database...");
        try {
            User savedUser = userRepository.save(user);
            System.out.println("✅ User successfully saved!");
            System.out.println("   - User ID: " + savedUser.getId());
            System.out.println("   - OpenID Subject: " + savedUser.getOpenIdSubject());
            System.out.println("   - Name: " + savedUser.getName());
            System.out.println("   - Email: " + savedUser.getEmail());
            System.out.println("   - Roles count: " + (savedUser.getRoles() != null ? savedUser.getRoles().size() : 0));
            System.out.println("   - Groups count: " + (savedUser.getGroups() != null ? savedUser.getGroups().size() : 0));

            System.out.println("🔍 === DEBUG: OAuth2 User Creation/Update Process END ===");
            return savedUser;

        } catch (Exception e) {
            System.err.println("❌ ERROR saving user to database!");
            System.err.println("   - Error message: " + e.getMessage());
            System.err.println("   - Error class: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw e;
        }
    }

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
        return userRepository.findByRoleName("TEACHER");
    }

    /**
     * Get all students
     */
    public List<User> getAllStudents() {
        return userRepository.findByRoleName("STUDENT");
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
     * Check if user has a specific role
     */
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}