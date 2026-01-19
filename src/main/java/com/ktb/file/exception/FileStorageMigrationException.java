package com.ktb.file.exception;

public class FileStorageMigrationException extends RuntimeException {
    public FileStorageMigrationException(String message) {
        super(message);
    }

    public FileStorageMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
