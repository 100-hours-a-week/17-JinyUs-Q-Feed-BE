package com.ktb.file.domain;

/**
 * 파일 업로드 상태
 */
public enum FileUploadStatus {
    /**
     * 업로드 대기 중 (Presigned URL 발급됨)
     */
    PENDING,

    /**
     * 업로드 완료
     */
    UPLOADED,

    /**
     * 업로드 실패
     */
    FAILED
}
