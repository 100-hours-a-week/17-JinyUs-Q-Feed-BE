package com.ktb.auth.service;

import com.ktb.auth.domain.UserAccount;

public interface UserAccountService {
    /**
     * 계정 조회
     */
    UserAccount findById(Long accountId);

    /**
     * 계정 생성 (OAuth 회원가입)
     */
    UserAccount createAccount(String email, String nickname);

    /**
     * 마지막 로그인 시간 갱신
     */
    void updateLastLoginAt(Long accountId);

    /**
     * 계정 비활성화
     */
    void deactivateAccount(Long accountId);
}
