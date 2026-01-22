package com.ktb.file.dto;

public record FileUploadResult(
    String fileUrl,
    String fileName,
    Long fileSize,
    String mimeType
) {
}
