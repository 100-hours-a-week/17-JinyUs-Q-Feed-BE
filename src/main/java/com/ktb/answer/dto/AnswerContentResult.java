package com.ktb.answer.dto;

import java.time.Instant;

public record AnswerContentResult(
        String answerText,
        String audioUrl,
        String videoUrl,
        Instant answeredAt
) {
}
