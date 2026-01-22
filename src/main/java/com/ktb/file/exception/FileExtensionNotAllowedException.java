package com.ktb.file.exception;

import static com.ktb.common.domain.ErrorCode.INVALID_FILE_FORMAT;

import com.ktb.common.exception.BusinessException;
import com.ktb.file.domain.FileCategory;

public class FileExtensionNotAllowedException extends BusinessException {
    public FileExtensionNotAllowedException() {
        super(INVALID_FILE_FORMAT);
    }

    public FileExtensionNotAllowedException(FileCategory category, String extension) {
        String errMsg = String.format(
            "%s 카테고리: %s, 확장자: %s, 허용 확장자: %s",
            INVALID_FILE_FORMAT.getMessage(),
            category.name(),
            extension,
            category.getAllowedExtensionsAsString()
        );

        super(INVALID_FILE_FORMAT, errMsg);
    }
}
