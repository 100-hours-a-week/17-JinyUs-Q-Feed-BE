package com.ktb.auth.domain;

import lombok.Getter;

/**
 * Token Family 무효화 사유
 */
@Getter
public enum RevokeReason {
    REUSE_DETECTED("토큰 재사용 감지"),
    USER_LOGOUT("사용자 로그아웃"),
    EXPIRED("만료"),
    ADMIN_ACTION("관리자 조치"),
    SECURITY_BREACH("보안 위반");

    private final String description;

    RevokeReason(String description) {
        this.description = description;
    }

}
