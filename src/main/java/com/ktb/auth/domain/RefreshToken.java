package com.ktb.auth.domain;

import com.ktb.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 엔티티 (RTR 방식)
 * - TokenFamily에 속한 개별 Refresh Token
 * - used 필드로 재사용 탐지
 */
@Entity
@Table(
        name = "USER_OAUTH_REFRESH",
        indexes = {
                @Index(name = "uk_token_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_family_id", columnList = "family_id"),
                @Index(name = "idx_used", columnList = "token_used"),
                @Index(name = "idx_expires_at", columnList = "token_expires_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private TokenFamily family;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "token_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "token_used", nullable = false)
    private Boolean used = false;

    @Column(name = "token_used_at")
    private LocalDateTime usedAt;

    @Builder
    private RefreshToken(
            TokenFamily family,
            String tokenHash,
            LocalDateTime expiresAt
    ) {
        this.family = family;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    /**
     * 토큰 사용 처리
     */
    public void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
