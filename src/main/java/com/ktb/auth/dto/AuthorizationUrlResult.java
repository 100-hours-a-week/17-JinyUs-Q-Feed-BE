package com.ktb.auth.dto;

/**
 * OAuth 인증 URL 응답
 */
public record AuthorizationUrlResult(
        String redirectUrl
) {
}
