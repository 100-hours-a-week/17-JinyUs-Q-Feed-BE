package com.ktb.ai.feedback.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "평가 지표")
public record AiFeedbackMetric(

        @JsonProperty("name")
        @Schema(description = "지표명", example = "정확도")
        String name,

        @JsonProperty("score")
        @Schema(description = "점수 (1~5)", example = "4", minimum = "1", maximum = "5")
        Integer score,

        @JsonProperty("comment")
        @Schema(description = "상세 코멘트", example = "RDBMS와 NoSQL의 핵심적인 차이점을 정확하게 설명했습니다...")
        String comment
) {
}
