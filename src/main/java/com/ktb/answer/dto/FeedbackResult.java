package com.ktb.answer.dto;

import java.time.Instant;
import java.util.Map;

public record FeedbackResult(
        FeedbackStatus status,
        Integer estimatedTimeSeconds,
        Map<String, Integer> metrics,
        String comment,
        Instant completedAt
) {
}
