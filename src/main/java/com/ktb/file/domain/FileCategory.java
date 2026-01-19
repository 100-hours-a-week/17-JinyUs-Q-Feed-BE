package com.ktb.file.domain;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum FileCategory {
    PROFILE(
            "프로필 이미지",
            5 * 1024 * 1024L, // 5MB
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp")
    ),

    ARCHITECTURE(
            "아키텍처 다이어그램",
            10 * 1024 * 1024L, // 10MB
            Arrays.asList("jpg", "jpeg", "png", "gif", "svg")
    ),

    ATTACHMENT(
            "일반 첨부파일",
            50 * 1024 * 1024L, // 50MB
            Arrays.asList("jpg", "jpeg", "png", "gif")
    ),

    TEMP(
            "임시 파일",
            100 * 1024 * 1024L, // 100MB
            Arrays.asList("jpg", "jpeg", "png", "gif")
    );

    private final String description;
    private final long maxSizeBytes;
    private final List<String> allowedExtensions;

    FileCategory(String description, long maxSizeBytes, List<String> allowedExtensions) {
        this.description = description;
        this.maxSizeBytes = maxSizeBytes;
        this.allowedExtensions = allowedExtensions;
    }

    public boolean isAllowedExtension(String extension) {
        if (extension == null) {
            return false;
        }
        return allowedExtensions.contains(extension.toLowerCase());
    }

    public double getMaxSizeMB() {
        return maxSizeBytes / (1024.0 * 1024.0);
    }

    public boolean isImageOnly() {
        return this == PROFILE;
    }

    public boolean shouldBeAutoDeleted() {
        return this == TEMP;
    }

    public String getAllowedExtensionsAsString() {
        return String.join(", ", allowedExtensions);
    }
}
