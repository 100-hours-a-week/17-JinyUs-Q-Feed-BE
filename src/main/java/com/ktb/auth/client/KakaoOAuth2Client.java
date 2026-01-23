package com.ktb.auth.client;

import com.ktb.auth.dto.response.KakaoUserInfoResponse;
import com.ktb.auth.dto.response.KakaoTokenResponse;
import com.ktb.auth.exception.oauth.OAuthProviderException;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2Client {

    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_CODE = "code";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String ERROR_TOKEN_RESPONSE_EMPTY = "Kakao Access Token을 받지 못했습니다.";
    private static final String ERROR_USERINFO_RESPONSE_EMPTY = "Kakao 사용자 정보를 받지 못했습니다.";

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

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(PARAM_GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        params.add(PARAM_CLIENT_ID, clientId);
        params.add(PARAM_CLIENT_SECRET, clientSecret);
        params.add(PARAM_REDIRECT_URI, redirectUri);
        params.add(PARAM_CODE, code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                KakaoTokenResponse.class
        );

        if (response.getBody() == null) {
            throw new OAuthProviderException(ERROR_TOKEN_RESPONSE_EMPTY);
        }

        return response.getBody().access_token();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserInfoResponse.class
        );

        if (response.getBody() == null) {
            throw new OAuthProviderException(ERROR_USERINFO_RESPONSE_EMPTY);
        }

        return response.getBody();
    }
}
