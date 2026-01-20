package com.ktb.auth.security.service;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 인증된 사용자 어댑터
 * - UserAccount를 AuthenticatedUser로 변환
 * - 도메인 모델과 보안 모델 분리
 */
@Getter
@RequiredArgsConstructor
public class AuthenticatedUserAdapter implements AuthenticatedUser {

    private final UserAccount account;
    private final List<String> roles;

    @Override
    public Long getUserId() {
        return account.getId();
    }

    @Override
    public String getEmail() {
        return account.getEmail();
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public boolean isActive() {
        return account.isActive();
    }

    /**
     * 원본 UserAccount 반환 (필요 시)
     */
    public UserAccount getAccount() {
        return account;
    }
}
