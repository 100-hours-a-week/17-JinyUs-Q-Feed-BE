package com.ktb.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증
 * - Access Token: userId + roles
 * - Refresh Token: userId + familyUuid
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(Long userId, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "ACCESS")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId, String familyUuid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("familyUuid", familyUuid)
                .claim("type", "REFRESH")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String type = claims.get("type", String.class);

            if (!"ACCESS".equals(type)) {
                throw new IllegalArgumentException("Not an Access Token");
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Access Token이 만료되었습니다.");
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException("잘못된 Access Token 서명입니다.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 Access Token입니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Access Token이 잘못되었습니다.");
        }
    }

    public Claims validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String type = claims.get("type", String.class);

            if (!"REFRESH".equals(type)) {
                throw new IllegalArgumentException("Not a Refresh Token");
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Refresh Token이 만료되었습니다.");
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException("잘못된 Refresh Token 서명입니다.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 Refresh Token입니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Refresh Token이 잘못되었습니다.");
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    public String getFamilyUuidFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("familyUuid", String.class);
    }

    /**
     * Token Hash 생성 (DB 저장용)
     */
    public String generateTokenHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
