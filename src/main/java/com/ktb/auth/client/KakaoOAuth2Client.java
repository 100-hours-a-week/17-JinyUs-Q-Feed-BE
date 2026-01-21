package com.ktb.auth.client;

import com.ktb.auth.dto.KakaoUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Kakao OAuth2 REST API Client
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2Client {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    /**
     * Authorization Code → Access Token
     */
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                KakaoTokenResponse.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Kakao Access Token을 받지 못했습니다.");
        }

        return response.getBody().getAccessToken();
    }

    /**
     * Access Token → 사용자 정보
     */
    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Kakao 사용자 정보를 받지 못했습니다.");
        }

        return response.getBody();
    }

    @Getter
    @NoArgsConstructor
    private static class KakaoTokenResponse {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private Integer expires_in;
        private String scope;

        public String getAccessToken() {
            return access_token;
        }
    }
}
