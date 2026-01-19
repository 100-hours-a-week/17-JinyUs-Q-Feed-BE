package com.ktb.auth.domain;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE("활성"),
    DORMANT("휴면"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

}
