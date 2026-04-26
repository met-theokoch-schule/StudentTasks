package com.example.studenttask.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthConfigurationStatusServiceTest {

    @Test
    void marksOauthConfigurationAsMissingWhenPlaceholdersRemainUnresolved() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.security.oauth2.client.registration.iserv.client-id", "${ISERV_CLIENT_ID}")
                .withProperty("spring.security.oauth2.client.registration.iserv.client-secret", "${ISERV_CLIENT_SECRET}")
                .withProperty("spring.security.oauth2.client.registration.iserv.redirect-uri", "https://example.invalid/login/oauth2/code/iserv");
        OAuthConfigurationStatusService service = new OAuthConfigurationStatusService(environment);

        assertThat(service.isReady()).isFalse();
        assertThat(service.getLoginPath()).isEqualTo("/oauth/setup");
        assertThat(service.getMissingSettings())
                .extracting(OAuthConfigurationStatusService.OAuthConfigurationSetting::environmentName)
                .containsExactly("ISERV_CLIENT_ID", "ISERV_CLIENT_SECRET");
    }

    @Test
    void marksOauthConfigurationAsReadyWhenIdSecretAndRedirectUriAreResolved() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("app.oauth.iserv.base-url", "https://schule.example")
                .withProperty("spring.security.oauth2.client.registration.iserv.client-id", "client-id")
                .withProperty("spring.security.oauth2.client.registration.iserv.client-secret", "client-secret")
                .withProperty("spring.security.oauth2.client.registration.iserv.redirect-uri", "https://example.invalid/login/oauth2/code/iserv");
        OAuthConfigurationStatusService service = new OAuthConfigurationStatusService(environment);

        assertThat(service.isReady()).isTrue();
        assertThat(service.getLoginPath()).isEqualTo("/oauth2/authorization/iserv");
        assertThat(service.getMissingSettings()).isEmpty();
        assertThat(service.getResolvedProviderBaseUrlExample()).isEqualTo("https://schule.example");
    }
}
