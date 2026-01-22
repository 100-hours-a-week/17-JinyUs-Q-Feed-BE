package com.ktb.file.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FileStorageMigrationException extends BusinessException {

    public FileStorageMigrationException(String message) {
        super(ErrorCode.FILE_STORAGE_MIGRATION_FAILED, message);
    }

    public FileStorageMigrationException(String message, Throwable cause) {
        super(ErrorCode.FILE_STORAGE_MIGRATION_FAILED, message, cause);
    }
}
