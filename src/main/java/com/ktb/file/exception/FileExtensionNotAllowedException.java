package com.ktb.file.exception;

import com.ktb.file.domain.FileCategory;

public class FileExtensionNotAllowedException extends RuntimeException {
    public FileExtensionNotAllowedException(String message) {
        super(message);
    }

    public FileExtensionNotAllowedException(FileCategory category, String extension) {
        super(String.format(
                "허용되지 않은 파일 확장자입니다. 카테고리: %s, 확장자: %s, 허용 확장자: %s",
                category.name(),
                extension,
                category.getAllowedExtensionsAsString()
        ));
    }
}
