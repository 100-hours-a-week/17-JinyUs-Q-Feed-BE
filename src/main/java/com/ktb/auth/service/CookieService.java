package com.ktb.auth.service;

import jakarta.servlet.http.Cookie;

/**
 * Cookie 관리 서비스
 */
public interface CookieService {

    /**
     * Refresh Token 쿠키 생성
     *
     * @param refreshToken Refresh Token 값
     * @return 생성된 쿠키
     */
    Cookie createRefreshTokenCookie(String refreshToken);

    /**
     * Refresh Token 쿠키 삭제를 위한 빈 쿠키 생성
     *
     * @return 만료된 쿠키
     */
    Cookie createExpiredRefreshTokenCookie();
}
