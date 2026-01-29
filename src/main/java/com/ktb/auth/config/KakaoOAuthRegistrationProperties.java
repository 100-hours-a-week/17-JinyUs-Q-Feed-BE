package com.ktb.auth.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
@Getter
@Setter
public class KakaoOAuthRegistrationProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scope = List.of();
}
