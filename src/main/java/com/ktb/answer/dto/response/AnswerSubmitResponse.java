package com.ktb.answer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "답변 제출 응답 (연습/단일 답변)")
public record AnswerSubmitResponse(

        @Schema(description = "생성된 답변 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long answerId,

        @Schema(description = "즉각 피드백 (키워드 체크)", requiredMode = Schema.RequiredMode.REQUIRED)
        ImmediateFeedback immediateFeedback,

        @Schema(description = "AI 피드백 처리 상태", example = "processing",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"processing", "completed", "failed"})
        String aiFeedbackStatus
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

    private static final String DEFAULT_AI_FEEDBACK_STATUS = "processing";

    public static AnswerSubmitResponse processing(Long answerId, ImmediateFeedback immediateFeedback) {
        return new AnswerSubmitResponse(
                answerId,
                immediateFeedback,
                DEFAULT_AI_FEEDBACK_STATUS
        );
    }
}
