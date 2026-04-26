package com.example.studenttask.controller;

import com.example.studenttask.config.OAuthConfigurationStatusService;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private OAuthConfigurationStatusService oauthConfigurationStatusService;

    @InjectMocks
    private HomeController controller;

    @Test
    void oauthSetup_populatesModelWithMissingSettingsAndInstructions() {
        Model model = new ExtendedModelMap();
        List<OAuthConfigurationStatusService.OAuthConfigurationSetting> missingSettings = List.of(
                new OAuthConfigurationStatusService.OAuthConfigurationSetting(
                        "Client ID",
                        "ISERV_CLIENT_ID",
                        "Die öffentliche OAuth-Client-ID aus IServ.",
                        false),
                new OAuthConfigurationStatusService.OAuthConfigurationSetting(
                        "Client Secret",
                        "ISERV_CLIENT_SECRET",
                        "Das geheime OAuth-Client-Secret aus IServ.",
                        false)
        );

        when(oauthConfigurationStatusService.getMissingSettings()).thenReturn(missingSettings);
        when(oauthConfigurationStatusService.getRequiredSettings()).thenReturn(missingSettings);
        when(oauthConfigurationStatusService.getOptionalSettings()).thenReturn(List.of(
                new OAuthConfigurationStatusService.OAuthConfigurationSetting(
                        "IServ-Basis-URL",
                        "ISERV_BASE_URL",
                        "Die Basisadresse des verwendeten IServ-Servers ohne abschließenden Slash.",
                        true)
        ));
        when(oauthConfigurationStatusService.getResolvedRedirectUriExample())
                .thenReturn("https://services-new.theokoch.schule/informatik/login/oauth2/code/iserv");
        when(oauthConfigurationStatusService.getResolvedProviderBaseUrlExample())
                .thenReturn("https://theokoch.schule");
        when(oauthConfigurationStatusService.getActiveProfileLabel()).thenReturn("prod");

        String view = controller.oauthSetup(model);

        assertThat(view).isEqualTo("oauth-setup-required");
        assertThat(model.getAttribute("oauthMissingSettings")).isEqualTo(missingSettings);
        assertThat(model.getAttribute("oauthRequiredSettings")).isEqualTo(missingSettings);
        assertThat(model.getAttribute("oauthOptionalSettings")).isNotNull();
        assertThat(model.getAttribute("redirectUriExample"))
                .isEqualTo("https://services-new.theokoch.schule/informatik/login/oauth2/code/iserv");
        assertThat(model.getAttribute("providerBaseUrlExample")).isEqualTo("https://theokoch.schule");
        assertThat(model.getAttribute("activeProfiles")).isEqualTo("prod");
    }

    @Test
    void debug_populatesModelWithResolvedUserAndGroups() {
        User user = user(1L, "Ada Lovelace", "oidc-ada", Set.of(role("ROLE_TEACHER")));
        Group group = group(7L, "Q2");
        OAuth2AuthenticationToken token = token("oidc-ada", "Ada Lovelace", "ada@example.org");

        when(userService.findByOpenIdSubject("oidc-ada")).thenReturn(Optional.of(user));
        when(groupService.getGroupsForUser(user)).thenReturn(List.of(group));
        when(userService.hasTeacherRole(user)).thenReturn(true);
        when(userService.hasStudentRole(user)).thenReturn(false);

        Model model = new ExtendedModelMap();
        String view = controller.debug(model, token);

        assertThat(view).isEqualTo("debug");
        assertThat(model.getAttribute("user")).isSameAs(user);
        assertThat(model.getAttribute("roles")).isEqualTo(user.getRoles());
        assertThat(model.getAttribute("groups")).isEqualTo(List.of(group));
        assertThat(model.getAttribute("isTeacher")).isEqualTo(true);
        assertThat(model.getAttribute("isStudent")).isEqualTo(false);
        assertThat(model.getAttribute("name")).isEqualTo("Ada Lovelace");
        assertThat(model.getAttribute("email")).isEqualTo("ada@example.org");
        assertThat(model.getAttribute("attributes")).isEqualTo(token.getPrincipal().getAttributes());
    }

    @Test
    void debug_throwsAuthenticationExceptionWhenTokenIsMissing() {
        assertThatThrownBy(() -> controller.debug(new ExtendedModelMap(), null))
            .isInstanceOf(UserAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void debug_throwsAuthenticationExceptionWhenSubjectIsMissing() {
        OAuth2AuthenticationToken token = tokenWithoutSubject("Ada Lovelace", "ada@example.org");

        assertThatThrownBy(() -> controller.debug(new ExtendedModelMap(), token))
            .isInstanceOf(UserAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void debug_keepsOauthAttributesWhenDatabaseUserIsMissing() {
        OAuth2AuthenticationToken token = token("oidc-missing", "New User", "new@example.org");

        when(userService.findByOpenIdSubject("oidc-missing")).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String view = controller.debug(model, token);

        assertThat(view).isEqualTo("debug");
        assertThat(model.getAttribute("user")).isNull();
        assertThat(model.getAttribute("name")).isEqualTo("New User");
        assertThat(model.getAttribute("email")).isEqualTo("new@example.org");
        assertThat(model.getAttribute("attributes")).isEqualTo(token.getPrincipal().getAttributes());
    }

    private OAuth2AuthenticationToken token(String subject, String name, String email) {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        OAuth2User principal = mock(OAuth2User.class);
        Map<String, Object> attributes = Map.of(
            "sub", subject,
            "name", name,
            "email", email
        );

        when(token.getPrincipal()).thenReturn(principal);
        when(principal.getAttribute("sub")).thenReturn(subject);
        when(principal.getAttribute("name")).thenReturn(name);
        when(principal.getAttribute("email")).thenReturn(email);
        when(principal.getAttributes()).thenReturn(attributes);

        return token;
    }

    private OAuth2AuthenticationToken tokenWithoutSubject(String name, String email) {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        OAuth2User principal = mock(OAuth2User.class);

        when(token.getPrincipal()).thenReturn(principal);
        when(principal.getAttribute("sub")).thenReturn(null);
        when(principal.getAttribute("name")).thenReturn(name);
        when(principal.getAttribute("email")).thenReturn(email);

        return token;
    }

    private User user(Long id, String name, String openIdSubject, Set<Role> roles) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setOpenIdSubject(openIdSubject);
        user.setRoles(roles);
        return user;
    }

    private Role role(String name) {
        return new Role(name, name);
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }
}
