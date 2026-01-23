package com.ktb.auth.dto.response;

public record KakaoTokenResponse(
    String access_token,
    String token_type,
    String refresh_token,
    Integer expires_in,
    String scope
) {
}
