package com.ktb.question.dto;

import java.util.List;

public record QuestionKeywordCheckResponse(
        List<KeywordMatchResponse> keywords
) {
}
