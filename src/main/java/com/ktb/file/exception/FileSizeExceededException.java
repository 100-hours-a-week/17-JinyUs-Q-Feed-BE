package com.ktb.file.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;
import com.ktb.file.domain.FileCategory;

public class FileSizeExceededException extends BusinessException {

    public FileSizeExceededException(FileCategory category, String fileSize) {
        String errMsg = String.format(
            "%s 카테고리: %s, 최대 크기: %.2f MB, 업로드 크기: %s",
            ErrorCode.FILE_SIZE_EXCEEDED,
            category.name(),
            category.getMaxSizeMB(),
            fileSize);
        super(ErrorCode.FILE_SIZE_EXCEEDED, errMsg);
    }
}
