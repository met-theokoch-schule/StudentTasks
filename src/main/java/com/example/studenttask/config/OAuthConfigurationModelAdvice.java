package com.example.studenttask.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class OAuthConfigurationModelAdvice {

    private final OAuthConfigurationStatusService oauthConfigurationStatusService;

    public OAuthConfigurationModelAdvice(OAuthConfigurationStatusService oauthConfigurationStatusService) {
        this.oauthConfigurationStatusService = oauthConfigurationStatusService;
    }

    @ModelAttribute("oauthConfigurationReady")
    public boolean oauthConfigurationReady() {
        return oauthConfigurationStatusService.isReady();
    }

    @ModelAttribute("oauthLoginPath")
    public String oauthLoginPath() {
        return oauthConfigurationStatusService.getLoginPath();
    }
}
