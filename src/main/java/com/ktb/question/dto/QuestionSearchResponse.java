package com.ktb.question.dto;

import java.util.List;

public record QuestionSearchResponse(
        List<QuestionSummaryResponse> questions,
        PaginationResponse pagination
) {
}
