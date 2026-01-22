package com.ktb.file.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FileNotFoundException extends BusinessException {

    public FileNotFoundException(Long fileId) {
        super(ErrorCode.FILE_NOT_FOUND, buildMessage(fileId));
    }

    public FileNotFoundException(String identifier) {
        super(ErrorCode.FILE_NOT_FOUND, buildMessage(identifier));
    }

    private static String buildMessage(Object identifier) {
        return String.format("%s identifier=%s",ErrorCode.FILE_NOT_FOUND, identifier);
    }
}
