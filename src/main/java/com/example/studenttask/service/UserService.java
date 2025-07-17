
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
                String displayName = (String) groupData.get("name");
                
                if (groupName != null) {
                    Group group = groupRepository.findByName(groupName)
                        .orElseGet(() -> {
                            Group newGroup = new Group(groupName, displayName);
                            return groupRepository.save(newGroup);
                        });
                    user.addGroup(group);
                }
            }
        }
    }
    
    public User findByOpenIdSubject(String openIdSubject) {
        return userRepository.findByOpenIdSubject(openIdSubject).orElse(null);
    }
    
    public List<User> findTeachers() {
        return userRepository.findByRoleName("ROLE_TEACHER");
    }
    
    public List<User> findStudents() {
        return userRepository.findByRoleName("ROLE_STUDENT");
    }
    
    public List<User> findByGroup(String groupName) {
        return userRepository.findByGroupName(groupName);
    }
}
