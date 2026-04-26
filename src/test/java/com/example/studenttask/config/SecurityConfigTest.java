package com.example.studenttask.config;

import com.example.studenttask.exception.OAuth2IdentityResolutionException;
import com.example.studenttask.service.IdentitySyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private IdentitySyncService identitySyncService;

    @Mock
    private OAuthConfigurationGuardFilter oauthConfigurationGuardFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void synchronizeOAuth2User_wrapsIdentityResolutionFailuresAsAuthenticationError() {
        OAuth2User oauth2User = oauth2User("oidc-broken");
        doThrow(new OAuth2IdentityResolutionException("OAuth2 user does not contain a subject"))
            .when(identitySyncService)
            .syncFromOAuth2User(oauth2User);

        assertThatThrownBy(() -> securityConfig.synchronizeOAuth2User(oauth2User))
            .isInstanceOf(OAuth2AuthenticationException.class)
            .hasMessage("OAuth2 user does not contain a subject");
    }

    @Test
    void resolveLoginFailurePath_returnsSpecificLoginMessageForInvalidUserInfo() {
        String path = securityConfig.resolveLoginFailurePath(
            new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"), "Broken OAuth2 identity")
        );

        assertThat(path).isEqualTo("/login?oauthIdentityError=true");
    }

    @Test
    void resolveLoginFailurePath_returnsGenericLoginMessageForOtherAuthenticationErrors() {
        String path = securityConfig.resolveLoginFailurePath(
            new OAuth2AuthenticationException(new OAuth2Error("server_error"), "OAuth2 login failed")
        );

        assertThat(path).isEqualTo("/login?error=true");
    }

    @Test
    void writeApiUnauthorizedResponse_returnsStandardizedJsonBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityConfig.writeApiUnauthorizedResponse(response);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString())
            .isEqualTo("{\"code\":\"unauthorized\",\"message\":\"Benutzer nicht gefunden\"}");
    }

    @Test
    void writeApiForbiddenResponse_returnsStandardizedJsonBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityConfig.writeApiForbiddenResponse(response);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString())
            .isEqualTo("{\"code\":\"forbidden\",\"message\":\"Zugriff verweigert\"}");
    }

    private OAuth2User oauth2User(String subject) {
        return mock(OAuth2User.class);
    }
}
