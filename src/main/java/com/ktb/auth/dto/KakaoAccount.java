package com.ktb.auth.dto;

/**
 * Kakao 사용자 계정 정보
 */
public record KakaoAccount(
        String email,
        KakaoProfile profile
) {
}
