package com.ktb.auth.service;

import com.ktb.auth.domain.OAuthProvider;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.dto.OAuthExchangePayload;
import com.ktb.auth.dto.response.KakaoUserInfoResponse;

public interface OAuthDomainService {

    /**
     * State 생성 및 저장 (CSRF 방지)
     */
    String generateAndStoreState(String provider);

    /**
     * State 검증 및 소비
     */
    void validateAndConsumeState(String state);

    /**
     * 교환 코드 생성 및 저장
     */
    String generateAndStoreExchangeCode(OAuthExchangePayload payload);

    /**
     * 교환 코드 검증 및 소비
     */
    OAuthExchangePayload consumeExchangeCode(String exchangeCode);

    /**
     * OAuth 사용자 조회 또는 생성
     */
    UserAccount findOrCreateAccount(OAuthProvider provider, String providerUserId, KakaoUserInfoResponse userInfo);

    /**
     * OAuth 연동 정보 갱신
     */
    void updateOAuthLoginInfo(Long oauthId);
}
