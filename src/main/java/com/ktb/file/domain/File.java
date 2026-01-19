package com.ktb.file.domain;

import com.ktb.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "FILE",
        indexes = {
                @Index(name = "idx_stored_name", columnList = "file_stored_name"),
                @Index(name = "idx_hash", columnList = "file_hash"),
                @Index(name = "idx_deleted", columnList = "file_deleted_at"),
                @Index(name = "idx_created", columnList = "file_created_at"),
                @Index(name = "idx_storage", columnList = "file_storage_type"),
                @Index(name = "idx_ext", columnList = "file_ext"),
                @Index(name = "idx_category", columnList = "file_category")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_original_name", nullable = false, length = 500)
    private String originalName;

    @Column(name = "file_stored_name", nullable = false, length = 500)
    private String storedName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String path;

    @Column(name = "file_ext", nullable = false, length = 20)
    private String extension;

    @Column(name = "file_size", nullable = false)
    private Long size;

    @Column(name = "file_hash", length = 64)
    private String hash;

    @Column(name = "file_mime_type", nullable = false, length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_storage_type", nullable = false, length = 20)
    private StorageType storageType = StorageType.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_category", nullable = false, length = 20)
    private FileCategory category;

    @Column(name = "file_url", length = 2000)
    private String url;

    @Column(name = "file_created_at", nullable = false, updatable = false)
    private LocalDateTime fileCreatedAt;

    @Column(name = "file_deleted_at")
    private LocalDateTime fileDeletedAt;

    @Builder
    private File(
            String originalName,
            String storedName,
            String path,
            String extension,
            Long size,
            String hash,
            String mimeType,
            StorageType storageType,
            FileCategory category,
            String url
    ) {
        validateFile(originalName, storedName, path, extension, size, mimeType);

        this.originalName = originalName;
        this.storedName = storedName;
        this.path = path;
        this.extension = extension.toLowerCase(); // 확장자는 소문자로 통일
        this.size = size;
        this.hash = hash;
        this.mimeType = mimeType;
        this.storageType = storageType != null ? storageType : StorageType.LOCAL;
        this.category = category;
        this.url = url;
        this.fileCreatedAt = LocalDateTime.now();
    }

    public static File create(
            String originalName,
            String storedName,
            String path,
            String extension,
            Long size,
            String mimeType,
            FileCategory category,
            StorageType storageType
    ) {
        return File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .path(path)
                .extension(extension)
                .size(size)
                .mimeType(mimeType)
                .category(category)
                .storageType(storageType)
                .build();
    }

    public static File createWithHash(
            String originalName,
            String storedName,
            String path,
            String extension,
            Long size,
            String hash,
            String mimeType,
            FileCategory category,
            StorageType storageType
    ) {
        return File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .path(path)
                .extension(extension)
                .size(size)
                .hash(hash)
                .mimeType(mimeType)
                .category(category)
                .storageType(storageType)
                .build();
    }

    private void validateFile(
            String originalName,
            String storedName,
            String path,
            String extension,
            Long size,
            String mimeType
    ) {
        if (originalName == null || originalName.trim().isEmpty()) {
            throw new IllegalArgumentException("원본 파일명은 필수입니다.");
        }
        if (originalName.length() > 500) {
            throw new IllegalArgumentException("원본 파일명은 500자를 초과할 수 없습니다.");
        }

        if (storedName == null || storedName.trim().isEmpty()) {
            throw new IllegalArgumentException("저장 파일명은 필수입니다.");
        }
        if (storedName.length() > 500) {
            throw new IllegalArgumentException("저장 파일명은 500자를 초과할 수 없습니다.");
        }

        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 경로는 필수입니다.");
        }
        if (path.length() > 1000) {
            throw new IllegalArgumentException("파일 경로는 1000자를 초과할 수 없습니다.");
        }

        if (extension == null || extension.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 확장자는 필수입니다.");
        }
        if (extension.length() > 20) {
            throw new IllegalArgumentException("파일 확장자는 20자를 초과할 수 없습니다.");
        }

        if (size == null || size < 0) {
            throw new IllegalArgumentException("파일 크기는 0 이상이어야 합니다.");
        }

        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new IllegalArgumentException("MIME 타입은 필수입니다.");
        }
        if (mimeType.length() > 100) {
            throw new IllegalArgumentException("MIME 타입은 100자를 초과할 수 없습니다.");
        }
    }

    public void updateUrl(String newUrl) {
        if (newUrl != null && newUrl.length() > 2000) {
            throw new IllegalArgumentException("URL은 2000자를 초과할 수 없습니다.");
        }
        this.url = newUrl;
    }

    public void migrateStorage(StorageType newStorageType, String newPath, String newUrl) {
        this.storageType = newStorageType;
        this.path = newPath;
        this.url = newUrl;
    }

    public void updateHash(String hash) {
        if (hash != null && hash.length() != 64) {
            throw new IllegalArgumentException("SHA-256 해시는 64자여야 합니다.");
        }
        this.hash = hash;
    }

    public void delete() {
        if (isDeleted()) {
            throw new IllegalStateException("이미 삭제된 파일입니다.");
        }
        this.fileDeletedAt = LocalDateTime.now();
    }

    public void restore() {
        if (!isDeleted()) {
            throw new IllegalStateException("삭제되지 않은 파일입니다.");
        }
        this.fileDeletedAt = null;
    }

    public boolean isDeleted() {
        return fileDeletedAt != null;
    }

    public boolean isActive() {
        return fileDeletedAt == null;
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }


    public boolean isVideo() {
        return mimeType != null && mimeType.startsWith("video/");
    }

    public String getReadableSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public void validateSizeLimit() {
        long maxSize = category.getMaxSizeBytes();
        if (size > maxSize) {
            throw new IllegalArgumentException(
                    String.format("파일 크기가 제한을 초과했습니다. (최대: %s, 현재: %s)",
                            formatBytes(maxSize),
                            getReadableSize())
            );
        }
    }

    public void validateExtension() {
        if (!category.isAllowedExtension(extension)) {
            throw new IllegalArgumentException(
                    String.format("허용되지 않은 파일 확장자입니다. (%s는 %s 카테고리에서 사용할 수 없습니다)",
                            extension,
                            category.name())
            );
        }
    }

    public long getDaysSinceCreated() {
        return java.time.Duration.between(fileCreatedAt, LocalDateTime.now()).toDays();
    }

    public boolean shouldBeCleanedAsTemp() {
        return category == FileCategory.TEMP && getDaysSinceCreated() >= 1;
    }

    public boolean shouldBePermanentlyDeleted() {
        if (!isDeleted()) {
            return false;
        }
        return java.time.Duration.between(fileDeletedAt, LocalDateTime.now()).toDays() >= 7;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
