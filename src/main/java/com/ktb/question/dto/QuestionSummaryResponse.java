package com.ktb.question.dto;

import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;

public record QuestionSummaryResponse(
        Long questionId,
        String content,
        QuestionType type,
        QuestionCategory category
) {
}
