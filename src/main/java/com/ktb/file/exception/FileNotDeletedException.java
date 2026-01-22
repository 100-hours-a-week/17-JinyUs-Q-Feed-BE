package com.ktb.file.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FileNotDeletedException extends BusinessException {

    public FileNotDeletedException(Long fileId) {
        super(ErrorCode.FILE_NOT_DELETED, String.format(
            "%s fileId=%d"
            ,ErrorCode.FILE_NOT_FOUND.getMessage()
            ,fileId
        ));
    }
}
