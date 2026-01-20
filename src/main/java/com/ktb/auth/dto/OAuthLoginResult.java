package com.ktb.auth.dto;

/**
 * OAuth 로그인 성공 응답
 */
public record OAuthLoginResult(
        String accessToken,
        String refreshToken,
        UserInfo user
) {
}
