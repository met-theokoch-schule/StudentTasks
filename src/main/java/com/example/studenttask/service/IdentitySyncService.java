package com.example.studenttask.service;

import com.example.studenttask.exception.OAuth2IdentityResolutionException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class IdentitySyncService {

    private static final Logger log = LoggerFactory.getLogger(IdentitySyncService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    public User syncFromOAuth2User(OAuth2User oauth2User) {
        String openIdSubject = oauth2User.getAttribute("sub");
        if (openIdSubject == null || openIdSubject.isBlank()) {
            throw new OAuth2IdentityResolutionException("OAuth2 user does not contain a subject");
        }

        User user = userRepository.findByOpenIdSubject(openIdSubject)
            .orElseGet(() -> {
                log.info("Creating new user from OAuth2 subject {}", openIdSubject);
                User newUser = new User();
                newUser.setOpenIdSubject(openIdSubject);
                return newUser;
            });

        if (user.getId() != null) {
            log.debug("Synchronizing existing user {} from OAuth2 subject {}", user.getId(), openIdSubject);
        }

        mapBasicAttributes(user, oauth2User);
        user.setRoles(resolveRoles(oauth2User));
        user.setGroups(resolveGroups(oauth2User));

        User savedUser = userRepository.save(user);
        log.debug("OAuth2 sync completed for subject {} with {} role(s) and {} group(s)",
            openIdSubject,
            savedUser.getRoles().size(),
            savedUser.getGroups().size());
        return savedUser;
    }

    private void mapBasicAttributes(User user, OAuth2User oauth2User) {
        user.setEmail(oauth2User.getAttribute("email"));
        user.setName(oauth2User.getAttribute("name"));
        user.setPreferredUsername(oauth2User.getAttribute("preferred_username"));
        user.setGivenName(oauth2User.getAttribute("given_name"));
        user.setFamilyName(oauth2User.getAttribute("family_name"));
    }

    @SuppressWarnings("unchecked")
    private Set<Role> resolveRoles(OAuth2User oauth2User) {
        List<Map<String, Object>> roles = oauth2User.getAttribute("roles");
        Set<Role> resolvedRoles = new HashSet<>();

        if (roles == null) {
            return resolvedRoles;
        }

        for (Map<String, Object> roleData : roles) {
            String roleId = (String) roleData.get("id");
            String displayName = (String) roleData.get("displayName");

            if (roleId == null || roleId.isBlank()) {
                continue;
            }

            Role role = roleRepository.findByName(roleId)
                .orElseGet(() -> roleRepository.save(new Role(
                    roleId,
                    displayName != null ? displayName : "Role from OAuth2: " + roleId
                )));
            resolvedRoles.add(role);
        }

        return resolvedRoles;
    }

    @SuppressWarnings("unchecked")
    private Set<Group> resolveGroups(OAuth2User oauth2User) {
        Map<String, Object> groups = oauth2User.getAttribute("groups");
        Set<Group> resolvedGroups = new HashSet<>();

        if (groups == null) {
            return resolvedGroups;
        }

        for (Object groupEntry : groups.values()) {
            Map<String, Object> groupData = (Map<String, Object>) groupEntry;
            String groupName = (String) groupData.get("act");
            String groupDescription = (String) groupData.get("desc");

            if (groupName == null || groupName.isBlank()) {
                continue;
            }

            Group group = groupRepository.findByName(groupName)
                .orElseGet(() -> groupRepository.save(new Group(groupName, groupDescription)));
            resolvedGroups.add(group);
        }

        return resolvedGroups;
    }
}
