package com.ktb.auth.domain;

import com.ktb.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * RTR Token Family 엔티티
 * - 디바이스별 세션 관리
 * - Reuse Detection 지원
 */
@Entity
@Table(
        name = "USER_OAUTH_RTR_FAMILY",
        indexes = {
                @Index(name = "uk_family_uuid", columnList = "family_uuid", unique = true),
                @Index(name = "idx_acct_id", columnList = "account_id"),
                @Index(name = "idx_revoked", columnList = "family_revoked"),
                @Index(name = "idx_expires_at", columnList = "family_expires_at"),
                @Index(name = "idx_last_used", columnList = "family_last_used_at"),
                @Index(name = "idx_acct_active",
                        columnList = "account_id, family_revoked, family_expires_at"),
                @Index(name = "idx_cleanup",
                        columnList = "family_revoked, family_expires_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"account"})
public class TokenFamily extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private Long id;

    @Column(name = "family_uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private UserAccount account;

    @Column(name = "family_device_info", length = 1000)
    private String deviceInfo;

    @Column(name = "family_client_ip", length = 45)
    private String clientIp;

    @Column(name = "family_last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @Column(name = "family_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "family_revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "family_revoked_at")
    private LocalDateTime revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_revoked_reason", length = 100)
    private RevokeReason revokedReason;

    @Builder
    private TokenFamily(
            UserAccount account,
            String deviceInfo,
            String clientIp,
            Integer familyLifetimeDays
    ) {
        this.uuid = UUID.randomUUID().toString();
        this.account = account;
        this.deviceInfo = deviceInfo;
        this.clientIp = clientIp;
        this.lastUsedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(
                familyLifetimeDays != null ? familyLifetimeDays : 30
        );
        this.revoked = false;
    }

    public static TokenFamily create(
            UserAccount account,
            String deviceInfo,
            String clientIp
    ) {
        return TokenFamily.builder()
                .account(account)
                .deviceInfo(deviceInfo)
                .clientIp(clientIp)
                .build();
    }

    public void updateLastUsed() {
        if (isRevoked()) {
            throw new IllegalStateException("무효화된 Family입니다.");
        }
        if (isExpired()) {
            throw new IllegalStateException("만료된 Family입니다.");
        }
        this.lastUsedAt = LocalDateTime.now();
    }

    public void revoke(RevokeReason reason) {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
        this.revokedReason = reason;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }
}
