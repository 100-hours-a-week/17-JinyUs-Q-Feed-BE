package com.ktb.answer.dto.request;

import com.ktb.answer.domain.AnswerType;
import com.ktb.question.domain.QuestionCategory;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 답변 목록 조회 요청 DTO
 */
@Schema(description = "답변 목록 조회 요청")
public record AnswerListRequest(

        @Parameter(description = "답변 타입 필터", example = "PRACTICE_INTERVIEW")
        @Schema(description = "답변 타입 (PRACTICE_INTERVIEW, REAL_INTERVIEW, PORTFOLIO_INTERVIEW)", example = "PRACTICE_INTERVIEW")
        AnswerType type,

        @Parameter(description = "질문 카테고리 필터", example = "OS")
        @Schema(description = "질문 카테고리 (OS, NETWORK, DB, COMPUTER_ARCHITECTURE, ALGORITHM, DATA_STRUCTURE)", example = "OS")
        QuestionCategory category,

        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", example = "2026-01-01")
        @Schema(description = "조회 시작 날짜", example = "2026-01-01", format = "date")
        LocalDate dateFrom,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", example = "2026-01-31")
        @Schema(description = "조회 종료 날짜", example = "2026-01-31", format = "date")
        LocalDate dateTo,

        @Parameter(description = "페이지 크기 (1-50)", example = "10")
        @Schema(description = "페이지 크기 (기본값: 10, 최대: 50)", example = "10", minimum = "1", maximum = "50")
        @Min(1) @Max(50)
        Integer limit,

        @Parameter(description = "다음 페이지 커서 (Base64 인코딩)", example = "eyJsYXN0X2NyZWF0ZWRfYXQiOiIyMDI2LTAxLTIyVDEwOjAwOjAwIiwibGFzdF9hbnN3ZXJfaWQiOjEwfQ==")
        @Schema(description = "페이지네이션 커서 (이전 응답의 nextCursor 값)", example = "eyJsYXN0X2NyZWF0ZWRfYXQiOiIyMDI2LTAxLTIyVDEwOjAwOjAwIiwibGFzdF9hbnN3ZXJfaWQiOjEwfQ==")
        String cursor,

        @Parameter(description = "확장 필드 (쉼표 구분: question,feedback)", example = "question,feedback")
        @Schema(description = "응답에 포함할 확장 필드 (question: 질문 정보, feedback: 피드백 요약)", example = "question,feedback")
        String expand
) {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    public AnswerListRequest {
        if (limit == null) {
            limit = DEFAULT_LIMIT;
        }
    }
}
