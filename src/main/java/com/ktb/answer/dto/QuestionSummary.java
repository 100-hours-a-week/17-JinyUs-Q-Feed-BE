package com.ktb.answer.dto;

public record QuestionSummary(
        Long questionId,
        String title,
        String content
) {
}
