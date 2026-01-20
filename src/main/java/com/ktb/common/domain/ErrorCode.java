package com.ktb.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==================== OAuth 관련 ====================
    UNSUPPORTED_PROVIDER(400, "AUTH001", "지원하지 않는 OAuth 제공자입니다"),
    INVALID_STATE(401, "AUTH002", "state 검증에 실패했습니다"),
    INVALID_AUTHORIZATION_CODE(401, "AUTH003", "authorization code가 유효하지 않습니다"),
    OAUTH_PROVIDER_ERROR(502, "AUTH004", "OAuth 제공자 통신에 실패했습니다"),

    // ==================== Token 관련 ====================
    INVALID_ACCESS_TOKEN(401, "AUTH011", "Access Token이 유효하지 않습니다"),
    INVALID_REFRESH_TOKEN(401, "AUTH012", "Refresh Token이 유효하지 않습니다"),
    MISSING_REFRESH_TOKEN(400, "AUTH013", "Refresh Token이 누락되었습니다"),
    TOKEN_REUSE_DETECTED(403, "AUTH014", "토큰 재사용이 탐지되었습니다"),

    // ==================== Family/Session 관련 ====================
    FAMILY_REVOKED(401, "AUTH021", "세션이 이미 종료되었습니다"),
    FAMILY_OWNERSHIP_MISMATCH(403, "AUTH022", "세션 소유권이 일치하지 않습니다"),

    // ==================== Account 관련 ====================
    ACCOUNT_NOT_FOUND(404, "AUTH031", "계정을 찾을 수 없습니다"),

    // ==================== Question 관련 ====================
    QUESTION_NOT_FOUND(404, "Q001", "질문을 찾을 수 없습니다"),
    QUESTION_DISABLED(400, "Q002", "비활성화된 질문입니다"),
    QUESTION_ALREADY_DELETED(400, "Q003", "이미 삭제된 질문입니다"),
    QUESTION_INVALID_CONTENT(400, "Q004", "질문 내용이 올바르지 않습니다"),

    // ==================== Search 관련 ====================
    SEARCH_KEYWORD_TOO_SHORT(400, "S001", "검색어는 너무 짧습니다"),
    SEARCH_FAILED(500, "S002", "검색에 실패했습니다"),

    // ==================== 공통 ====================
    // ==================== 공통 (COMMON) ====================
    INVALID_INPUT(400, "C001", "입력값이 올바르지 않습니다"),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다"),
    FORBIDDEN(403, "C004", "접근 권한이 없습니다"),
    UNAUTHORIZED(401, "AUTH901", "인증이 필요합니다"),
    TOO_MANY_REQUESTS(429, "AUTH903", "요청이 너무 많습니다");

    private final int status;
    private final String code;
    private final String message;
}
