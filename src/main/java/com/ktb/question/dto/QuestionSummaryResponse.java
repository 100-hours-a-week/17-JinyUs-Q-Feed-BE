package com.ktb.question.dto;

import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import java.util.List;

public record QuestionSummaryResponse(
        Long questionId,
        String content,
        QuestionType type,
        QuestionCategory category,
        List<String> keywords
) {
}
