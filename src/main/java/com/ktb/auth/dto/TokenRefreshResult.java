package com.ktb.auth.dto;

public record TokenRefreshResult(
        String accessToken,
        String refreshToken,
        int expiresIn
) {
}
