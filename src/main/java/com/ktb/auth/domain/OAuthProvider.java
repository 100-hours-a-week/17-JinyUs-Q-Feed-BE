package com.ktb.auth.domain;

import lombok.Getter;

/**
 * OAuth 제공자 Enum
 */
@Getter
public enum OAuthProvider {
    KAKAO("카카오");

    private final String displayName;

    OAuthProvider(String displayName) {
        this.displayName = displayName;
    }

}
