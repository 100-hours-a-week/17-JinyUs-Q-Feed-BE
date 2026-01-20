package com.ktb.auth.service.impl;

import com.ktb.auth.jwt.JwtProperties;
import com.ktb.auth.service.CookieService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Cookie 관리 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    private final JwtProperties jwtProperties;

    @Override
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송 (프로덕션)
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge((int) (jwtProperties.getRefreshTokenExpiration() / 1000)); // 밀리초 → 초
        // cookie.setAttribute("SameSite", "Strict"); // Spring Boot 3.x+에서 지원
        return cookie;
    }

    @Override
    public Cookie createExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0); // 즉시 만료
        return cookie;
    }
}
