package com.ktb.file.exception;

import com.ktb.file.domain.FileCategory;

public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException(FileCategory category, long fileSize) {
        super(String.format(
                "파일 크기가 제한을 초과했습니다. 카테고리: %s, 최대 크기: %.2f MB, 업로드 크기: %.2f MB",
                category.name(),
                category.getMaxSizeMB(),
                fileSize / (1024.0 * 1024.0)
        ));
    }
}
