package com.ktb.auth.service;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenService {

    /**
     * Access Token 발급
     */
    String issueAccessToken(Long accountId, List<String> roles);

    /**
     * Access Token 검증
     */
    TokenClaims validateAccessToken(String accessToken);

    /**
     * Refresh Token 검증
     */
    RefreshTokenClaims validateRefreshToken(String refreshToken);

    /**
     * Refresh Token Hash로 DB 조회
     */
    RefreshTokenEntity findByTokenHash(String tokenHash);

    // DTO 레코드
    record TokenClaims(Long userId, List<String> roles) {}
    record RefreshTokenClaims(Long userId, String familyUuid) {}
    record RefreshTokenEntity(Long id, Long familyId, Boolean used, LocalDateTime expiresAt) {}
}

