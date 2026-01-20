package com.ktb.question.dto;

import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;

public record QuestionUpdateRequest(
        String content,
        QuestionType type,
        QuestionCategory category,
        Boolean useYn
) {
}
