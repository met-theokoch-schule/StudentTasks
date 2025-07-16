
package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginTestController {

    @GetMapping("/test/login")
    public String loginTest(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("oauth2User", principal);
            model.addAttribute("attributes", principal.getAttributes());
            model.addAttribute("authorities", principal.getAuthorities());
            
            User user = (User) principal.getAttribute("user");
            model.addAttribute("user", user);
        }
        return "test/login-test";
    }
}
