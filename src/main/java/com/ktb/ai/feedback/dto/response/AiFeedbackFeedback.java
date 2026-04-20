package com.ktb.ai.feedback.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "종합 피드백")
public record AiFeedbackFeedback(

        @JsonProperty("strengths")
        @Schema(description = "강점", example = "• RDBMS와 NoSQL의 핵심적인 차이점을 정확하게 이해하고 설명했습니다...")
        String strengths,

        @JsonProperty("improvements")
        @Schema(description = "개선사항", example = "• 각 데이터베이스의 특징을 나열하는 것을 넘어, 인과관계를 함께 설명하면...")
        String improvements
) {
}
