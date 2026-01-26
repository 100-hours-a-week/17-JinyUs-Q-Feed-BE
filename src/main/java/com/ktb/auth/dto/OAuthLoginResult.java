package com.ktb.auth.dto;

public record OAuthLoginResult(
        String accessToken,
        String refreshToken,
        UserInfo user
) {
}
