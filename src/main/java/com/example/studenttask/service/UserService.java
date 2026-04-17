package com.example.studenttask.service;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.GroupRepository;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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
        String openIdSubject = oauth2User.getAttribute("sub");
        log.debug("Mapping OAuth2 attributes for subject {}", openIdSubject);
        logOAuth2AttributeDump(oauth2User.getAttributes());

        // Standard Claims mapping
        user.setOpenIdSubject(openIdSubject);
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
        if (roles == null) {
            log.debug("No role data received from OAuth2 for subject {}", user.getOpenIdSubject());
            return;
        }

        log.debug("Processing {} role(s) from OAuth2 for subject {}", roles.size(), user.getOpenIdSubject());

        for (Map<String, Object> roleData : roles) {
            String roleId = (String) roleData.get("id");
            String displayName = (String) roleData.get("displayName");

            if (roleId != null) {
                Role role = roleRepository.findByName(roleId)
                        .orElseGet(() -> {
                            log.debug("Creating new role {}", roleId);
                            Role newRole = new Role(roleId, displayName);
                            return roleRepository.save(newRole);
                        });
                user.addRole(role);
                log.debug("Assigned role {} to user {}", role.getName(), user.getOpenIdSubject());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void mapGroupsFromOAuth2(User user, OAuth2User oauth2User) {
        Set<Group> oldGroups = new HashSet<>(user.getGroups());
        log.debug("Synchronizing groups from OAuth2 for subject {}. Previous group count: {}",
                user.getOpenIdSubject(), oldGroups.size());

        if (log.isDebugEnabled()) {
            oldGroups.forEach(group -> log.debug("Previous group: {}", group.getName()));
        }

        // Clear existing groups
        user.getGroups().clear();

        Map<String, Map<String, Object>> groups = oauth2User.getAttribute("groups");
        if (groups == null) {
            log.debug("No group data received from OAuth2 for subject {}", user.getOpenIdSubject());
            return;
        }

        log.debug("Processing {} group(s) from OAuth2 for subject {}",
                groups.size(), user.getOpenIdSubject());

        for (Map.Entry<String, Map<String, Object>> groupEntry : groups.entrySet()) {
            Map<String, Object> groupData = groupEntry.getValue();
            String groupName = (String) groupData.get("act");
            String groupDescription = (String) groupData.get("desc");

            log.debug("Processing group {} ({})", groupName, groupDescription);

            if (groupName != null) {
                Group group = groupRepository.findByName(groupName)
                        .orElseGet(() -> {
                            log.debug("Creating new group {}", groupName);
                            Group newGroup = new Group(groupName, groupDescription);
                            return groupRepository.save(newGroup);
                        });

                user.addGroup(group);
                log.debug("Assigned group {} to user {}", group.getName(), user.getOpenIdSubject());
            }
        }

        log.debug("Group synchronization complete for subject {}. New group count: {}",
                user.getOpenIdSubject(), user.getGroups().size());

        if (log.isDebugEnabled()) {
            user.getGroups().forEach(group -> log.debug("Current group: {}", group.getName()));
        }
    }

    public User findUserByOpenIdSubject(String openIdSubject) {
        log.debug("Looking up user by OpenID subject {}", openIdSubject);
        Optional<User> userOpt = userRepository.findByOpenIdSubject(openIdSubject);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.debug("Found user {} for subject {}", user.getId(), openIdSubject);
            return user;
        }

        log.debug("No user found for subject {}", openIdSubject);
        return null;
    }

    public User createOrUpdateUserFromOAuth2(OAuth2User oauth2User) {
        String openIdSubject = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String preferredUsername = oauth2User.getAttribute("preferred_username");

        log.debug("Starting OAuth2 user synchronization for subject {}", openIdSubject);
        log.debug("OAuth2 attribute summary for subject {}: name={}, email={}, preferredUsername={}",
                openIdSubject, name, email, preferredUsername);
        logOAuth2AttributeDump(oauth2User.getAttributes());

        Optional<User> userOpt = userRepository.findByOpenIdSubject(openIdSubject);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("Updating existing user {} from OAuth2", user.getId());
        } else {
            log.info("Creating new user from OAuth2 subject {}", openIdSubject);
            user = new User();
            user.setOpenIdSubject(openIdSubject);
        }

        // Set basic user attributes
        user.setName(name);
        user.setEmail(email);
        user.setGivenName(givenName);
        user.setFamilyName(familyName);
        user.setPreferredUsername(preferredUsername);

        // Handle roles from OAuth2 attributes
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rolesList = oauth2User.getAttribute("roles");
        if (rolesList != null) {
            Set<Role> roles = new HashSet<>();
            log.debug("Processing {} role(s) for subject {}", rolesList.size(), openIdSubject);

            for (Map<String, Object> roleData : rolesList) {
                String roleId = (String) roleData.get("id");
                if (roleId != null) {
                    Optional<Role> roleOpt = roleRepository.findByName(roleId);
                    Role role = null;

                    if (roleOpt.isPresent()) {
                        role = roleOpt.get();
                        log.debug("Using existing role {} with id {}", role.getName(), role.getId());
                    }

                    if (role == null) {
                        log.debug("Creating new role {}", roleId);
                        role = new Role();
                        role.setName(roleId);
                        role.setDescription("Role from OAuth2: " + roleId);
                        role = roleRepository.save(role);
                        log.debug("Created role {} with id {}", roleId, role.getId());
                    }
                    roles.add(role);
                }
            }
            user.setRoles(roles);
            log.debug("Assigned {} role(s) to user {}", roles.size(), openIdSubject);
        } else {
            log.debug("No roles data received from OAuth2 for subject {}", openIdSubject);
        }

        // Handle groups from OAuth2 attributes - CLEAR OLD GROUPS FIRST
        log.debug("Synchronizing groups for subject {}. Current group count before sync: {}",
                openIdSubject, user.getGroups().size());
        if (log.isDebugEnabled()) {
            user.getGroups().forEach(group -> log.debug("Existing group before sync: {}", group.getName()));
        }

        // Clear existing groups to ensure fresh sync
        user.getGroups().clear();

        @SuppressWarnings("unchecked")
        Map<String, Object> groupsData = oauth2User.getAttribute("groups");
        if (groupsData != null) {
            Set<Group> groups = new HashSet<>();
            log.debug("Processing {} group(s) for subject {}", groupsData.size(), openIdSubject);

            for (Map.Entry<String, Object> entry : groupsData.entrySet()) {
                log.debug("Processing group entry {}", entry.getKey());
                @SuppressWarnings("unchecked")
                Map<String, Object> groupInfo = (Map<String, Object>) entry.getValue();
                String groupName = (String) groupInfo.get("act");

                if (groupName != null) {
                    Optional<Group> groupOpt = groupRepository.findByName(groupName);
                    Group group = null;

                    if (groupOpt.isPresent()) {
                        group = groupOpt.get();
                        log.debug("Using existing group {} with id {}", group.getName(), group.getId());
                    }
                    if (group == null) {
                        log.debug("Creating new group {}", groupName);
                        group = new Group();
                        group.setName(groupName);
                        group.setDescription("Group from OAuth2: " + groupName);
                        group = groupRepository.save(group);
                        log.debug("Created group {} with id {}", groupName, group.getId());
                    }
                    groups.add(group);
                }
            }
            user.setGroups(groups);
            log.debug("Assigned {} group(s) to user {}", groups.size(), openIdSubject);
            if (log.isDebugEnabled()) {
                groups.forEach(group -> log.debug("Assigned group: {}", group.getName()));
            }
        } else {
            log.debug("No groups data received from OAuth2 for subject {}", openIdSubject);
        }

        // Save user with all updated information
        user = userRepository.save(user);
        log.info("Completed OAuth2 synchronization for user {}", user.getId());
        log.debug("Final group count for user {}: {}", user.getId(), user.getGroups().size());
        if (log.isDebugEnabled()) {
            user.getGroups().forEach(group -> log.debug("Final group: {}", group.getName()));
        }

        return user;
    }

    private void logOAuth2AttributeDump(Map<String, Object> allAttributes) {
        if (!log.isDebugEnabled() || allAttributes == null) {
            return;
        }

        log.debug("OAuth2 token contains {} attribute(s)", allAttributes.size());
        for (Map.Entry<String, Object> entry : allAttributes.entrySet()) {
            Object value = entry.getValue();
            log.debug("OAuth2 attribute [{}] = {}", entry.getKey(), value);
            log.debug("OAuth2 attribute [{}] type = {}", entry.getKey(),
                    value != null ? value.getClass().getName() : "null");

            if (value instanceof Object[]) {
                log.debug("OAuth2 attribute [{}] array content = {}", entry.getKey(),
                        Arrays.deepToString((Object[]) value));
            } else if (value instanceof List<?>) {
                log.debug("OAuth2 attribute [{}] list content = {}", entry.getKey(), value);
            } else if (value instanceof Map<?, ?>) {
                log.debug("OAuth2 attribute [{}] map content = {}", entry.getKey(), value);
            }
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
