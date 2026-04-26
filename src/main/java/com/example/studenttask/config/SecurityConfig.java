package com.example.studenttask.config;

import com.example.studenttask.dto.ApiErrorResponseDto;
import com.example.studenttask.exception.OAuth2IdentityResolutionException;
import jakarta.servlet.http.HttpServletResponse;
import com.example.studenttask.service.IdentitySyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private IdentitySyncService identitySyncService;

    @Autowired
    private OAuthConfigurationGuardFilter oauthConfigurationGuardFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/error", "/oauth/setup", "/webjars/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService()))
                        .successHandler((request, response, authentication) -> {
                            log.debug("OAuth2 success handler called for {}", authentication.getName());
                            String contextPath = request.getContextPath();
                            response.sendRedirect(contextPath + "/dashboard");
                        })
                        .failureHandler((request, response, exception) -> {
                            String contextPath = request.getContextPath();
                            response.sendRedirect(contextPath + resolveLoginFailurePath(exception));
                        }))
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                (request, response, exception) -> writeApiUnauthorizedResponse(response),
                                new AntPathRequestMatcher("/api/**"))
                        .defaultAccessDeniedHandlerFor(
                                (request, response, exception) -> writeApiForbiddenResponse(response),
                                new AntPathRequestMatcher("/api/**"))
                        .accessDeniedPage("/access-denied"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired=true"))
                .addFilterBefore(oauthConfigurationGuardFilter, OAuth2AuthorizationRequestRedirectFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return new OAuth2UserService<OAuth2UserRequest, OAuth2User>() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oauth2User = delegate.loadUser(userRequest);

                synchronizeOAuth2User(oauth2User);

                return oauth2User;
            }
        };
    }

    void synchronizeOAuth2User(OAuth2User oauth2User) {
        try {
            identitySyncService.syncFromOAuth2User(oauth2User);
        } catch (OAuth2IdentityResolutionException exception) {
            log.warn("OAuth2 user synchronization failed: {}", exception.getMessage());
            throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_user_info"),
                exception.getMessage()
            );
        }
    }

    String resolveLoginFailurePath(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException oauth2AuthenticationException
                && "invalid_user_info".equals(oauth2AuthenticationException.getError().getErrorCode())) {
            return "/login?oauthIdentityError=true";
        }

        return "/login?error=true";
    }

    void writeApiUnauthorizedResponse(HttpServletResponse response) throws IOException {
        writeApiErrorResponse(
            response,
            HttpStatus.UNAUTHORIZED,
            new ApiErrorResponseDto("unauthorized", "Benutzer nicht gefunden")
        );
    }

    void writeApiForbiddenResponse(HttpServletResponse response) throws IOException {
        writeApiErrorResponse(
            response,
            HttpStatus.FORBIDDEN,
            new ApiErrorResponseDto("forbidden", "Zugriff verweigert")
        );
    }

    private void writeApiErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            ApiErrorResponseDto errorResponse) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            "{\"code\":\"" + errorResponse.getCode() + "\",\"message\":\"" + errorResponse.getMessage() + "\"}"
        );
    }
}
