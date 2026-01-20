package com.ktb.auth.dto;

public record UserInfo(
        String nickname,
        boolean isNewUser
) {
}
