package com.example.studenttask.service;

import com.example.studenttask.exception.OAuth2IdentityResolutionException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.GroupRepository;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentitySyncServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private IdentitySyncService identitySyncService;

    @Test
    void syncFromOAuth2User_createsMissingUserRoleAndGroupFromOAuth2Data() {
        OAuth2User oauth2User = oauth2User(Map.of(
            "sub", "oidc-1",
            "name", "Ada Teacher",
            "email", "ada@example.com",
            "preferred_username", "ada",
            "given_name", "Ada",
            "family_name", "Lovelace",
            "roles", List.of(Map.of("id", "ROLE_TEACHER", "displayName", "Teacher")),
            "groups", Map.of("g1", Map.of("act", "10A", "desc", "Klasse 10A"))
        ));

        when(userRepository.findByOpenIdSubject("oidc-1")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.empty());
        when(groupRepository.findByName("10A")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(11L);
            return role;
        });
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
            Group group = invocation.getArgument(0);
            group.setId(21L);
            return group;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(31L);
            return user;
        });

        User savedUser = identitySyncService.syncFromOAuth2User(oauth2User);

        assertThat(savedUser.getId()).isEqualTo(31L);
        assertThat(savedUser.getOpenIdSubject()).isEqualTo("oidc-1");
        assertThat(savedUser.getName()).isEqualTo("Ada Teacher");
        assertThat(savedUser.getEmail()).isEqualTo("ada@example.com");
        assertThat(savedUser.getPreferredUsername()).isEqualTo("ada");
        assertThat(savedUser.getRoles()).extracting(Role::getName).containsExactly("ROLE_TEACHER");
        assertThat(savedUser.getRoles()).extracting(Role::getDescription).containsExactly("Teacher");
        assertThat(savedUser.getGroups()).extracting(Group::getName).containsExactly("10A");
        assertThat(savedUser.getGroups()).extracting(Group::getDescription).containsExactly("Klasse 10A");
    }

    @Test
    void syncFromOAuth2User_replacesExistingRolesAndGroupsOnExistingUser() {
        Role oldRole = role(1L, "ROLE_OLD", "old");
        Role newRole = role(2L, "ROLE_STUDENT", "Student");
        Group oldGroup = group(10L, "Alt", "Alt");
        Group newGroup = group(11L, "11B", "Klasse 11B");

        User existingUser = new User();
        existingUser.setId(7L);
        existingUser.setOpenIdSubject("oidc-2");
        existingUser.setRoles(Set.of(oldRole));
        existingUser.setGroups(Set.of(oldGroup));

        OAuth2User oauth2User = oauth2User(Map.of(
            "sub", "oidc-2",
            "name", "Grace Student",
            "email", "grace@example.com",
            "preferred_username", "grace",
            "given_name", "Grace",
            "family_name", "Hopper",
            "roles", List.of(Map.of("id", "ROLE_STUDENT", "displayName", "Student")),
            "groups", Map.of("g1", Map.of("act", "11B", "desc", "Klasse 11B"))
        ));

        when(userRepository.findByOpenIdSubject("oidc-2")).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(newRole));
        when(groupRepository.findByName("11B")).thenReturn(Optional.of(newGroup));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = identitySyncService.syncFromOAuth2User(oauth2User);

        assertThat(savedUser).isSameAs(existingUser);
        assertThat(savedUser.getName()).isEqualTo("Grace Student");
        assertThat(savedUser.getRoles()).containsExactly(newRole);
        assertThat(savedUser.getGroups()).containsExactly(newGroup);
        assertThat(savedUser.getRoles()).doesNotContain(oldRole);
        assertThat(savedUser.getGroups()).doesNotContain(oldGroup);
    }

    @Test
    void syncFromOAuth2User_throwsIdentityResolutionExceptionWhenSubjectIsMissing() {
        OAuth2User oauth2User = oauth2User(Map.of(
            "name", "Broken User",
            "email", "broken@example.com"
        ));

        assertThatThrownBy(() -> identitySyncService.syncFromOAuth2User(oauth2User))
            .isInstanceOf(OAuth2IdentityResolutionException.class)
            .hasMessage("OAuth2 user does not contain a subject");
    }

    private OAuth2User oauth2User(Map<String, Object> attributes) {
        return new OAuth2User() {
            @Override
            public Map<String, Object> getAttributes() {
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getName() {
                return String.valueOf(attributes.get("sub"));
            }
        };
    }

    private Role role(Long id, String name, String description) {
        Role role = new Role(name, description);
        role.setId(id);
        return role;
    }

    private Group group(Long id, String name, String description) {
        Group group = new Group(name, description);
        group.setId(id);
        return group;
    }
}
