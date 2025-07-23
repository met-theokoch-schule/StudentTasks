package com.example.studenttask.config;

import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.studenttask.model.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/error", "/webjars/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oauth2UserService())
                )
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("🔄 Custom OAuth2 Success Handler called");
            System.out.println("🔍 Authentication type: " + authentication.getClass().getName());
            System.out.println("🔍 Principal type: " + authentication.getPrincipal().getClass().getName());
            System.out.println("🔍 Current authorities: " + authentication.getAuthorities());

            // Log each authority individually
            authentication.getAuthorities().forEach(authority -> {
                System.out.println("   👤 Authority: " + authority.getAuthority() + " (Type: " + authority.getClass().getName() + ")");
            });

            response.sendRedirect("/dashboard");
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return new OAuth2UserService<OAuth2UserRequest, OAuth2User>() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                System.out.println("🔄 OAuth2UserService.loadUser() called");

                OAuth2User oauth2User = delegate.loadUser(userRequest);

                // Log OAuth2 attributes
                System.out.println("🔍 OAuth2 User Attributes:");
                oauth2User.getAttributes().forEach((key, value) -> {
                    System.out.println("   " + key + ": " + value);
                });

                // Create or update user in database
                User user = userService.findOrCreateUserFromOAuth2(oauth2User);
                System.out.println("✅ User created/updated: " + user.getName());

                // Authorities aus OAuth2 User Attributes extrahieren
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("OIDC_USER"));

                // Rollen aus OAuth2-Attributen extrahieren
                Object rolesObj = oauth2User.getAttribute("roles");
                if (rolesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> rolesList = (List<Map<String, Object>>) rolesObj;

                    for (Map<String, Object> roleMap : rolesList) {
                        String roleId = (String) roleMap.get("id");
                        if (roleId != null) {
                            authorities.add(new SimpleGrantedAuthority(roleId));
                            System.out.println("   🔑 Added authority from OAuth2: " + roleId);
                        }
                    }
                }

                // Zusätzlich: Authorities aus User-Datenbank laden (falls vorhanden)
                if (user != null && user.getRoles() != null) {
                    user.getRoles().forEach(role -> {
                        SimpleGrantedAuthority dbAuthority = new SimpleGrantedAuthority(role.getName());
                        if (!authorities.contains(dbAuthority)) {
                            authorities.add(dbAuthority);
                            System.out.println("   🔑 Added additional authority from DB: " + role.getName());
                        }
                    });
                }

                // Return DefaultOAuth2User with correct authorities
                return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "name");
            }
        };
    }
}