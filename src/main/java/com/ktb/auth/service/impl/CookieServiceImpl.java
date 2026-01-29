package com.ktb.auth.service.impl;

import com.ktb.auth.jwt.JwtProperties;
import com.ktb.auth.service.CookieService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    private final JwtProperties jwtProperties;

    @Value("${cookie.secure:true}")
    private boolean cookieSecure;

    @Override
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge((int) (jwtProperties.getRefreshTokenExpiration() / 1000));
        cookie.setAttribute("SameSite", cookieSecure ? "Strict" : "Lax");
        return cookie;
    }

    @Override
    public Cookie createExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", cookieSecure ? "Strict" : "Lax");
        return cookie;
    }
}
