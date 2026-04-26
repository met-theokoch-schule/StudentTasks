package com.example.studenttask.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthConfigurationGuardFilterTest {

    @Mock
    private OAuthConfigurationStatusService oauthConfigurationStatusService;

    @Mock
    private FilterChain filterChain;

    @Test
    void redirectsAuthorizationRequestToSetupPageWhenOauthConfigurationIsMissing() throws Exception {
        OAuthConfigurationGuardFilter filter = new OAuthConfigurationGuardFilter(oauthConfigurationStatusService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/iserv");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(oauthConfigurationStatusService.isReady()).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getRedirectedUrl()).isEqualTo("/oauth/setup");
        verifyNoInteractions(filterChain);
    }

    @Test
    void allowsAuthorizationRequestWhenOauthConfigurationIsReady() throws Exception {
        OAuthConfigurationGuardFilter filter = new OAuthConfigurationGuardFilter(oauthConfigurationStatusService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/iserv");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(oauthConfigurationStatusService.isReady()).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getRedirectedUrl()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
