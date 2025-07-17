
package com.example.taskmanagement.config;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("===>>> Configuring SecurityFilterChain with AuthenticationSuccessHandler approach");

        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/error", "/webjars/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/test/login").permitAll()
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                              Authentication authentication) throws IOException, ServletException {
                
                System.out.println("=== AUTHENTICATION SUCCESS HANDLER CALLED ===");
                
                if (authentication.getPrincipal() instanceof OAuth2User) {
                    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                    
                    System.out.println("=== OAuth2 User Processing ===");
                    System.out.println("Principal: " + oauth2User.getName());
                    System.out.println("Attributes: " + oauth2User.getAttributes());
                    
                    // Extract user data
                    String subject = oauth2User.getAttribute("sub");
                    String name = oauth2User.getAttribute("name");
                    String email = oauth2User.getAttribute("email");
                    String preferredUsername = oauth2User.getAttribute("preferred_username");
                    String givenName = oauth2User.getAttribute("given_name");
                    String familyName = oauth2User.getAttribute("family_name");
                    
                    System.out.println("Subject: " + subject);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    
                    // Extract roles
                    Set<String> roleNames = new HashSet<>();
                    Object rolesObj = oauth2User.getAttribute("roles");
                    if (rolesObj instanceof List<?>) {
                        for (Object roleObj : (List<?>) rolesObj) {
                            if (roleObj instanceof Map<?, ?>) {
                                Map<?, ?> roleMap = (Map<?, ?>) roleObj;
                                String roleId = (String) roleMap.get("id");
                                if (roleId != null) {
                                    String roleName = roleId.startsWith("ROLE_") ? roleId.substring(5) : roleId;
                                    roleNames.add(roleName);
                                    System.out.println("Extracted role: " + roleName);
                                }
                            }
                        }
                    }
                    
                    // Extract groups
                    Set<String> groupNames = new HashSet<>();
                    Object groupsObj = oauth2User.getAttribute("groups");
                    if (groupsObj instanceof Map<?, ?>) {
                        Map<?, ?> groupsMap = (Map<?, ?>) groupsObj;
                        for (Object groupObj : groupsMap.values()) {
                            if (groupObj instanceof Map<?, ?>) {
                                Map<?, ?> groupMap = (Map<?, ?>) groupObj;
                                String actName = (String) groupMap.get("act");
                                if (actName != null) {
                                    groupNames.add(actName);
                                    System.out.println("Extracted group: " + actName);
                                }
                            }
                        }
                    }
                    
                    // Create or update user
                    try {
                        User user = userService.createOrUpdateUser(subject, name, email, preferredUsername, 
                                                                 givenName, familyName, roleNames, groupNames);
                        System.out.println("User created/updated successfully: " + user.getId());
                        
                        // Store user in session
                        request.getSession().setAttribute("user", user);
                        System.out.println("User stored in session");
                        
                    } catch (Exception e) {
                        System.err.println("Error creating/updating user: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                System.out.println("=== Redirecting to /dashboard ===");
                response.sendRedirect("/dashboard");
            }
        };
    }
}
