package com.ktb.file.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FileAlreadyDeletedException extends BusinessException {

    public FileAlreadyDeletedException(Long fileId) {
        super(ErrorCode.FILE_ALREADY_DELETED, buildMessage(fileId));
    }

    public FileAlreadyDeletedException(String identifier) {
        super(ErrorCode.FILE_ALREADY_DELETED, buildMessage(identifier));
    }

    private static String buildMessage(Object identifier) {
        return String.format("%s identifier=%s",ErrorCode.FILE_ALREADY_DELETED , identifier);
    }
}
