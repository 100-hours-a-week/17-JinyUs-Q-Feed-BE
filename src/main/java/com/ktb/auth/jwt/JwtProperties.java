package com.ktb.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 프로퍼티
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * JWT 서명 비밀키 (최소 256비트)
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 600000 (10분)
     */
    private Long accessTokenExpiration = 600000L;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 1209600000 (14일)
     */
    private Long refreshTokenExpiration = 1209600000L;

    /**
     * JWT Issuer
     */
    private String issuer = "QFeed";

    /**
     * Refresh Token 쿠키 이름
     */
    private String refreshTokenCookieName = "refreshToken";
}
