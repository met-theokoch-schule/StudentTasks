package com.example.studenttask.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OAuthConfigurationStatusService {

    private static final String AUTHORIZATION_PATH = "/oauth2/authorization/iserv";
    private static final String SETUP_PATH = "/oauth/setup";
    private static final String DEFAULT_ISERV_BASE_URL = "https://theokoch.schule";
    private static final String PRODUCTION_REDIRECT_EXAMPLE =
            "https://services-new.theokoch.schule/informatik/login/oauth2/code/iserv";

    private final Environment environment;

    public OAuthConfigurationStatusService(Environment environment) {
        this.environment = environment;
    }

    public boolean isReady() {
        return getMissingSettings().isEmpty();
    }

    public String getLoginPath() {
        return isReady() ? AUTHORIZATION_PATH : SETUP_PATH;
    }

    public List<OAuthConfigurationSetting> getMissingSettings() {
        return getRequiredSettings().stream()
                .filter(setting -> !setting.configured())
                .toList();
    }

    public List<OAuthConfigurationSetting> getRequiredSettings() {
        return List.of(
                setting(
                        "Client ID",
                        "spring.security.oauth2.client.registration.iserv.client-id",
                        "ISERV_CLIENT_ID",
                        "Die öffentliche OAuth-Client-ID aus IServ."),
                setting(
                        "Client Secret",
                        "spring.security.oauth2.client.registration.iserv.client-secret",
                        "ISERV_CLIENT_SECRET",
                        "Das geheime OAuth-Client-Secret aus IServ."),
                setting(
                        "Redirect-URI",
                        "spring.security.oauth2.client.registration.iserv.redirect-uri",
                        "ISERV_REDIRECT_URI",
                        "Die bei IServ hinterlegte Callback-URL dieser Anwendung."));
    }

    public List<OAuthConfigurationSetting> getOptionalSettings() {
        return List.of(
                setting(
                        "IServ-Basis-URL",
                        "app.oauth.iserv.base-url",
                        "ISERV_BASE_URL",
                        "Die Basisadresse des verwendeten IServ-Servers ohne abschließenden Slash."));
    }

    public String getResolvedRedirectUriExample() {
        String redirectUri = getPropertySafely("spring.security.oauth2.client.registration.iserv.redirect-uri");
        return isResolved(redirectUri) ? redirectUri : PRODUCTION_REDIRECT_EXAMPLE;
    }

    public String getResolvedProviderBaseUrlExample() {
        String providerBaseUrl = getPropertySafely("app.oauth.iserv.base-url");
        return isResolved(providerBaseUrl) ? providerBaseUrl : DEFAULT_ISERV_BASE_URL;
    }

    public String getActiveProfileLabel() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return "default";
        }
        return String.join(", ", activeProfiles);
    }

    private OAuthConfigurationSetting setting(
            String label,
            String propertyName,
            String environmentName,
            String description) {
        String value = getPropertySafely(propertyName);
        return new OAuthConfigurationSetting(
                label,
                environmentName,
                description,
                isResolved(value));
    }

    private String getPropertySafely(String propertyName) {
        try {
            return environment.getProperty(propertyName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean isResolved(String value) {
        return value != null && !value.isBlank() && !value.contains("${");
    }

    public record OAuthConfigurationSetting(
            String label,
            String environmentName,
            String description,
            boolean configured) {
    }
}
