package com.ktb.auth.domain;

import com.ktb.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 사용자 계정 엔티티
 * - OAuth 및 일반 로그인 통합 관리
 * - Soft Delete 방식 채택
 * - 프로필 이미지는 File 엔티티로 분리 관리
 */
@Entity
@Table(
        name = "USER_ACCOUNT",
        indexes = {
                @Index(name = "uk_email", columnList = "account_email", unique = true),
                @Index(name = "idx_status", columnList = "account_status_cd, deleted_at"),
                @Index(name = "idx_file_id", columnList = "file_id"),
                @Index(name = "idx_created_at", columnList = "created_at"),
                @Index(name = "idx_last_login", columnList = "account_last_login_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"oauthConnections"})
public class UserAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_email", length = 320, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status_cd", nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "account_nick_nm", nullable = false, length = 200)
    private String nickname;

    @Column(name = "account_last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "account")
    private final List<UserOAuth> oauthConnections = new ArrayList<>();

    @Builder
    private UserAccount(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
        this.status = AccountStatus.ACTIVE;
    }

    public static UserAccount createEmailAccount(String email, String nickname) {
        return UserAccount.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (newNickname.length() > 200) {
            throw new IllegalArgumentException("닉네임은 200자를 초과할 수 없습니다.");
        }
        this.nickname = newNickname;
    }

    public void updateStatus(AccountStatus newStatus) {
        this.status = newStatus;
    }

    public void markAsDormant() {
        this.status = AccountStatus.DORMANT;
    }

    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    public void withdraw() {
        this.status = AccountStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public void addOAuthConnection(UserOAuth oauth) {
        oauthConnections.add(oauth);
        oauth.setAccount(this);
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE && deletedAt == null;
    }

    public boolean shouldBeDormant() {
        if (lastLoginAt == null) {
            return false; // 한 번도 로그인 안 한 경우는 제외
        }
        return lastLoginAt.isBefore(LocalDateTime.now().minusYears(1));
    }
}
