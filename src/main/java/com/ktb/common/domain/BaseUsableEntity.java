package com.ktb.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 사용 여부(use_yn)를 관리하는 베이스 엔티티
 * - created_at (생성 일시)
 * - updated_at (수정 일시)
 * - use_yn (사용 여부, Soft Disable)
 *
 * 적용 대상: HASHTAG, METRIC
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseUsableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;

    public boolean isUsable() {
        return useYn;
    }

    public void disable() {
        this.useYn = false;
    }

    public void enable() {
        this.useYn = true;
    }
}
