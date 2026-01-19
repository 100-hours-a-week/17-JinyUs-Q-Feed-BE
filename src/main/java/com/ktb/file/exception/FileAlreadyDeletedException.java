package com.ktb.file.exception;

public class FileAlreadyDeletedException extends RuntimeException {
    public FileAlreadyDeletedException(String message) {
        super(message);
    }
}
