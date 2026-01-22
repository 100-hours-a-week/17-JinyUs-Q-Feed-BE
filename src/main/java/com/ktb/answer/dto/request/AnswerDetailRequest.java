package com.ktb.answer.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 답변 상세 조회 요청 DTO
 */
@Schema(description = "답변 상세 조회 요청")
public record AnswerDetailRequest(

        @Parameter(description = "확장 필드 (쉼표 구분: question,feedback,immediate_feedback)",
                   example = "question,feedback,immediate_feedback")
        @Schema(description = "응답에 포함할 확장 필드 (question: 질문 정보, feedback: AI 피드백, immediate_feedback: 즉각 피드백)",
                example = "question,feedback,immediate_feedback")
        String expand
) {

    private static final String EXPAND_QUESTION = "question";
    private static final String EXPAND_FEEDBACK = "feedback";
    private static final String EXPAND_IMMEDIATE_FEEDBACK = "immediate_feedback";
}
