package com.ktb.answer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 실전 세션 기반 답변 제출 응답 DTO
 */
@Schema(description = "실전 세션 기반 답변 제출 응답")
public record SessionAnswerSubmitResponse(

        @Schema(description = "생성된 답변 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long answerId,

        @Schema(description = "비디오 파일 URL (비디오 파일 제출 시)", example = "https://cdn.example.com/video/123.mp4")
        String videoUrl,

        @Schema(description = "즉각 피드백 (키워드 체크)", requiredMode = Schema.RequiredMode.REQUIRED)
        ImmediateFeedback immediateFeedback,

        @Schema(description = "AI 피드백 처리 상태", example = "processing",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"processing", "completed", "failed"})
        String aiFeedbackStatus,

        @Schema(description = "다음 질문 정보 (세션 계속 진행 시)")
        NextQuestion nextQuestion
) {

    @Schema(description = "즉각 피드백 정보")
    public record ImmediateFeedback(
            @Schema(description = "키워드 체크 결과 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<KeywordCheck> keywords
    ) {
    }

    @Schema(description = "키워드 체크 결과")
    public record KeywordCheck(
            @Schema(description = "키워드", example = "프로세스")
            String keyword,

            @Schema(description = "답변에 포함 여부", example = "true")
            boolean included
    ) {
    }

    @Schema(description = "다음 질문 정보")
    public record NextQuestion(
            @Schema(description = "다음 질문 ID", example = "15")
            Long questionId,

            @Schema(description = "다음 질문 내용", example = "동기화 기법에 대해 설명해주세요")
            String content,

            @Schema(description = "다음 질문 카테고리", example = "OS")
            String category
    ) {
    }

    private static final String DEFAULT_AI_FEEDBACK_STATUS = "processing";

    public static SessionAnswerSubmitResponse processing(
            Long answerId,
            ImmediateFeedback immediateFeedback,
            NextQuestion nextQuestion
    ) {
        return new SessionAnswerSubmitResponse(
                answerId,
                null,
                immediateFeedback,
                DEFAULT_AI_FEEDBACK_STATUS,
                nextQuestion
        );
    }
}
