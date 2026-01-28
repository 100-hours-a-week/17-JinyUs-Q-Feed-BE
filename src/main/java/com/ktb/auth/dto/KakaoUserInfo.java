package com.ktb.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kakao 사용자 정보 응답 record
 */
public record KakaoUserInfo(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

    public String getProviderId() {
        return id != null ? String.valueOf(id) : null;
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.email() : null;
    }

    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.profile() != null
                ? kakaoAccount.profile().nickname()
                : null;
    }

    public String getProfileImageUrl() {
        return kakaoAccount != null && kakaoAccount.profile() != null
                ? kakaoAccount.profile().profileImageUrl()
                : null;
    }
}
