package com.ktb.ai.feedback.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "AI 피드백 API 응답")
public record AiFeedbackApiResponse(

        @JsonProperty("status")
        @Schema(description = "응답 상태", example = "success", allowableValues = {"success", "error"})
        String status,

        @JsonProperty("feedback")
        @Schema(description = "AI 종합 평가 피드백",
                example = "전반적으로 RDBMS와 NoSQL의 핵심 차이점을 잘 설명하셨습니다...")
        String feedback,

        @JsonProperty("radar_chart")
        @Schema(description = "레이더 차트 평가 지표 목록")
        List<RadarMetric> radarChart,

        @JsonProperty("error_message")
        @Schema(description = "에러 메시지 (status=error 시)")
        String errorMessage
) {

    @Schema(description = "레이더 차트 평가 지표")
    public record RadarMetric(

            @JsonProperty("metric_name")
            @Schema(description = "평가 지표명", example = "논리 구조")
            String metricName,

            @JsonProperty("metric_description")
            @Schema(description = "평가 지표 설명", example = "답변의 논리적 흐름과 구조")
            String metricDescription,

            @JsonProperty("score")
            @Schema(description = "획득 점수", example = "85", minimum = "0", maximum = "100")
            int score,

            @JsonProperty("max_score")
            @Schema(description = "최대 점수", example = "100")
            int maxScore
    ) {
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
