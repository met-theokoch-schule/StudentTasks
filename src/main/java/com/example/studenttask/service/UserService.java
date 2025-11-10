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
        System.out.println("==========================================");
        System.out.println("🔍 COMPLETE OAUTH2 TOKEN DUMP:");
        System.out.println("==========================================");
        Map<String, Object> allAttributes = oauth2User.getAttributes();
        for (Map.Entry<String, Object> entry : allAttributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println("   [" + key + "] = " + value);
            System.out.println("      Type: " + (value != null ? value.getClass().getName() : "null"));
            if (value != null && value.getClass().isArray()) {
                System.out.println("      Array content: " + java.util.Arrays.toString((Object[]) value));
            } else if (value instanceof java.util.List) {
                System.out.println("      List content: " + value);
            } else if (value instanceof java.util.Map) {
                System.out.println("      Map content: " + value);
            }
        }
        System.out.println("==========================================");

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
        System.out.println("👥 === Synchronizing Groups from OAuth2 ===");

        // Get current groups before clearing
        Set<Group> oldGroups = new HashSet<>(user.getGroups());
        System.out.println("   - Old groups count: " + oldGroups.size());
        oldGroups.forEach(g -> System.out.println("     - Old: " + g.getName()));

        // Clear existing groups
        user.getGroups().clear();

        Map<String, Map<String, Object>> groups = oauth2User.getAttribute("groups");
        System.out.println("   - Groups from OAuth2: " + groups);

        if (groups != null) {
            System.out.println("   - Processing " + groups.size() + " group(s) from OAuth2");

            for (Map.Entry<String, Map<String, Object>> groupEntry : groups.entrySet()) {
                Map<String, Object> groupData = groupEntry.getValue();
                String groupName = (String) groupData.get("act");
                String groupDescription = (String) groupData.get("desc");

                System.out.println("   - Processing group: " + groupName + " (desc: " + groupDescription + ")");

                if (groupName != null) {
                    Group group = groupRepository.findByName(groupName)
                        .orElseGet(() -> {
                            System.out.println("     - Creating NEW group: " + groupName);
                            Group newGroup = new Group(groupName, groupDescription);
                            return groupRepository.save(newGroup);
                        });

                    System.out.println("     - Adding group to user: " + group.getName() + " (ID: " + group.getId() + ")");
                    user.addGroup(group);
                }
            }

            System.out.println("   - New groups count: " + user.getGroups().size());
            user.getGroups().forEach(g -> System.out.println("     - New: " + g.getName()));
        } else {
            System.out.println("   - No groups data received from OAuth2");
        }

        System.out.println("👥 === Groups Synchronization Complete ===");
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

        System.out.println("==========================================");
        System.out.println("🔍 COMPLETE OAUTH2 TOKEN DUMP:");
        System.out.println("==========================================");
        Map<String, Object> allAttributes = oauth2User.getAttributes();
        for (Map.Entry<String, Object> entry : allAttributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println("   [" + key + "] = " + value);
            System.out.println("      Type: " + (value != null ? value.getClass().getName() : "null"));
            if (value != null && value.getClass().isArray()) {
                System.out.println("      Array content: " + java.util.Arrays.toString((Object[]) value));
            } else if (value instanceof java.util.List) {
                System.out.println("      List content: " + value);
            } else if (value instanceof java.util.Map) {
                System.out.println("      Map content: " + value);
            }
        }
        System.out.println("==========================================");

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

        // Handle groups from OAuth2 attributes - CLEAR OLD GROUPS FIRST
        System.out.println("👥 Processing groups...");
        System.out.println("   - Current groups BEFORE sync: " + user.getGroups().size());
        user.getGroups().forEach(g -> System.out.println("     - Before: " + g.getName()));

        // Clear existing groups to ensure fresh sync
        user.getGroups().clear();
        System.out.println("   - Groups cleared, count: " + user.getGroups().size());

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
            groups.forEach(g -> System.out.println("     - Assigned: " + g.getName()));
        } else {
            System.out.println("   - No groups data received from OAuth2");
        }

        // Save user with all updated information
        System.out.println("💾 Saving user to database...");
        user = userRepository.save(user);
        System.out.println("✅ User saved successfully with ID: " + user.getId());
        System.out.println("   - Final groups count: " + user.getGroups().size());
        user.getGroups().forEach(g -> System.out.println("     - Final: " + g.getName()));

        System.out.println("🔍 === DEBUG: OAuth2 User Creation/Update Process COMPLETE ===");
        return user;
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
     * Überprüft, ob ein Benutzer (identifiziert durch openIdSubject) eine Lehrerrolle hat
     */
    public boolean hasTeacherRole(String openIdSubject) {
        Optional<User> userOpt = findByOpenIdSubject(openIdSubject);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
            .anyMatch(role -> {
                String roleName = role.getName();
                return "ROLE_TEACHER".equals(roleName) || 
                       "teacher".equals(roleName) || 
                       "lehrer".equals(roleName) ||
                       roleName.toLowerCase().contains("teacher") ||
                       roleName.toLowerCase().contains("lehrer");
            });
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

    public User findByPreferredUsername(String preferredUsername) {
        return userRepository.findByPreferredUsername(preferredUsername);
    }
}