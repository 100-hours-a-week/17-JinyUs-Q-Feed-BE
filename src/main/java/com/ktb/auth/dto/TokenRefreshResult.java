package com.ktb.auth.dto;

public record TokenRefreshResult(
        String accessToken,
        int expiresIn
) {
}
