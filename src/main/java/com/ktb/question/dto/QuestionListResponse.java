package com.ktb.question.dto;

import java.util.List;

public record QuestionListResponse(
        List<QuestionSummaryResponse> questions,
        PaginationResponse pagination
) {
}
