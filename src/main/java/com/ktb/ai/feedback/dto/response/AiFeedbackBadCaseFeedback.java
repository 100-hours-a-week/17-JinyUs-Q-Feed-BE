package com.ktb.ai.feedback.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bad case 피드백")
public record AiFeedbackBadCaseFeedback(

        @JsonProperty("type")
        @Schema(description = "Bad case 타입", example = "REFUSE_TO_ANSWER",
                allowableValues = {"REFUSE_TO_ANSWER", "TOO_SHORT", "INAPPROPRIATE"})
        String type,

        @JsonProperty("message")
        @Schema(description = "메시지", example = "답변이 감지되지 않았습니다.")
        String message,

        @JsonProperty("guidance")
        @Schema(description = "가이드", example = "질문에 대한 답변을 입력해주세요...")
        String guidance
) {
    public BadCaseType getTypeEnum() {
        return BadCaseType.valueOf(type);
    }
}
