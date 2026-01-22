package com.ktb.answer.dto;

import java.util.Map;

public record AiFeedbackSummary(
        FeedbackStatus status,
        Map<String, Integer> radarChart,
        String comment
) {
}
