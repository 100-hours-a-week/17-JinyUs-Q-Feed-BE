package com.ktb.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Kakao 사용자 정보 응답
 */
@Getter
@NoArgsConstructor
public class KakaoUserInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }

    public String getProviderId() {
        return String.valueOf(id);
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null
                ? kakaoAccount.getProfile().getNickname()
                : null;
    }

    public String getProfileImageUrl() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null
                ? kakaoAccount.getProfile().getProfileImageUrl()
                : null;
    }
}
