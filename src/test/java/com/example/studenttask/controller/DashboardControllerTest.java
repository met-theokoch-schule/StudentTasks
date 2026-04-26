package com.example.studenttask.controller;

import com.example.studenttask.exception.OAuth2IdentityResolutionException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.service.IdentitySyncService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private IdentitySyncService identitySyncService;

    @InjectMocks
    private DashboardController controller;

    @Test
    void dashboard_redirectsTeacherWithoutResyncWhenUserAlreadyExists() {
        User teacher = user(1L, "Teacher", Set.of(role("teacher")));

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(userService.hasTeacherRole(teacher)).thenReturn(true);
        when(userService.hasStudentRole(teacher)).thenReturn(false);

        Model model = new ExtendedModelMap();
        String view = controller.dashboard(oauthAuthentication("oidc-teacher"), model);

        assertThat(view).isEqualTo("redirect:/teacher/dashboard");
        verifyNoInteractions(identitySyncService);
    }

    @Test
    void dashboard_syncsMissingOauthUserAndRedirectsStudent() {
        User student = user(2L, "Student", Set.of(role("ROLE_STUDENT")));
        OAuth2AuthenticationToken authentication = oauthAuthentication("oidc-student");

        when(userService.findByOpenIdSubject("oidc-student")).thenReturn(Optional.empty());
        when(identitySyncService.syncFromOAuth2User(authentication.getPrincipal())).thenReturn(student);
        when(userService.hasTeacherRole(student)).thenReturn(false);
        when(userService.hasStudentRole(student)).thenReturn(true);

        Model model = new ExtendedModelMap();
        String view = controller.dashboard(authentication, model);

        assertThat(view).isEqualTo("redirect:/student/dashboard");
    }

    @Test
    void dashboard_throwsAuthenticationExceptionWhenNonOauthUserIsMissing() {
        when(userService.findByOpenIdSubject("missing-subject")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.dashboard(new TestingAuthenticationToken("principal", "credentials", "ROLE_USER") {
            @Override
            public String getName() {
                return "missing-subject";
            }
        }, new ExtendedModelMap()))
            .isInstanceOf(UserAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
        verifyNoInteractions(identitySyncService);
    }

    @Test
    void dashboard_throwsAuthenticationExceptionWhenOauthIdentityCannotBeSynchronized() {
        OAuth2AuthenticationToken authentication = oauthAuthentication("oidc-broken");

        when(userService.findByOpenIdSubject("oidc-broken")).thenReturn(Optional.empty());
        when(identitySyncService.syncFromOAuth2User(authentication.getPrincipal()))
            .thenThrow(new OAuth2IdentityResolutionException("OAuth2 user does not contain a subject"));

        assertThatThrownBy(() -> controller.dashboard(authentication, new ExtendedModelMap()))
            .isInstanceOf(UserAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    private OAuth2AuthenticationToken oauthAuthentication(String subject) {
        OAuth2User oauth2User = new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            Map.of("sub", subject),
            "sub"
        );
        return new OAuth2AuthenticationToken(
            oauth2User,
            oauth2User.getAuthorities(),
            "test-client"
        );
    }

    private User user(Long id, String name, Set<Role> roles) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setRoles(roles);
        return user;
    }

    private Role role(String name) {
        return new Role(name, name);
    }
}
