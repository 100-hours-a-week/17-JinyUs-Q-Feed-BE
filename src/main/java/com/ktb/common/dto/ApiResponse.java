package com.ktb.common.dto;

/**
 * 공통 API 응답
 */
public record ApiResponse<T>(
        String message,
        T data
) {
}
