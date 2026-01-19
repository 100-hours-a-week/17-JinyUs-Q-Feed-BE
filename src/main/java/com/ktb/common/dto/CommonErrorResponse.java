package com.ktb.common.dto;

import com.ktb.common.domain.ErrorCode;
import java.time.Instant;

public record CommonErrorResponse(
        String code,          // 에러 코드 (예: "S001")
        String message,       // 에러 메시지
        String detail,        // 상세 설명 (Optional)
        Instant timestamp,    // 발생 시각
        String path           // 요청 경로
) {

    public static CommonErrorResponse of(ErrorCode errorCode, String detail, String path) {
        return new CommonErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                detail,
                Instant.now(),
                path
        );
    }

    public static CommonErrorResponse of(ErrorCode errorCode, String path) {
        return of(errorCode, null, path);
    }
}
