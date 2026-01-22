package com.ktb.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseActivatableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;

    public boolean isDeleted() {
        return deletedAt == null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.useYn = false;  // 삭제 시 자동으로 비활성화
    }

    public void restore() {
        this.deletedAt = null;
    }

    public boolean isEnabled() {
        return useYn && isDeleted();
    }

    public void enable() {
        if (isDeleted()) {
            this.useYn = true;
        }
    }

    public void disable() {
        this.useYn = false;
    }
}
