package com.ktb.question.dto;

import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import java.time.LocalDateTime;
import java.util.List;

public record QuestionDetailResponse(
        Long questionId,
        String content,
        QuestionType type,
        QuestionCategory category,
        List<String> keywords,
        boolean useYn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
