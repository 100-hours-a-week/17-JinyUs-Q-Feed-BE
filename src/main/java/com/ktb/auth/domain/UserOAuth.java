package com.ktb.auth.domain;

import com.ktb.auth.exception.oauth.OAuthAlreadyUnlinkedException;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "USER_OAUTH",
        indexes = {
                @Index(name = "uk_oauth_provider_user",
                        columnList = "oauth_provider_nm, oauth_provider_user_id",
                        unique = true),
                @Index(name = "idx_acct_id", columnList = "account_id"),
                @Index(name = "idx_provider", columnList = "oauth_provider_nm"),
                @Index(name = "idx_last_login", columnList = "oauth_last_login_at"),
                @Index(name = "idx_unlinked", columnList = "oauth_unlinked_at"),
                @Index(name = "idx_acct_provider", columnList = "account_id, oauth_provider_nm"),
                @Index(name = "idx_acct_active", columnList = "account_id, oauth_unlinked_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "account")
public class UserOAuth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private UserAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider_nm", nullable = false, length = 30)
    private OAuthProvider provider;

    @Column(name = "oauth_provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(name = "oauth_connected_at", nullable = false, updatable = false)
    private LocalDateTime connectedAt;

    @Column(name = "oauth_last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "oauth_unlinked_at")
    private LocalDateTime unlinkedAt;

    @Builder
    private UserOAuth(
            UserAccount account,
            OAuthProvider provider,
            String providerUserId
    ) {
        this.account = account;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.connectedAt = LocalDateTime.now();
    }

    public static UserOAuth create(
            UserAccount account,
            OAuthProvider provider,
            String providerUserId
    ) {
        UserOAuth oauth = UserOAuth.builder()
                .account(account)
                .provider(provider)
                .providerUserId(providerUserId)
                .build();

        account.addOAuthConnection(oauth);
        return oauth;
    }

    protected void setAccount(UserAccount account) {
        this.account = account;
    }

    public void updateLastLogin() {
        if (isUnlinked()) {
            throw new OAuthAlreadyUnlinkedException(id);
        }
        this.lastLoginAt = LocalDateTime.now();
    }

    public void unlink() {
        this.unlinkedAt = LocalDateTime.now();
    }

    public void relink() {
        this.unlinkedAt = null;
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isUnlinked() {
        return unlinkedAt != null;
    }

    public boolean isActive() {
        return unlinkedAt == null;
    }
}
