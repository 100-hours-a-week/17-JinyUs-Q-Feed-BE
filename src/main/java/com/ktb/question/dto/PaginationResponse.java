package com.ktb.question.dto;

public record PaginationResponse(
        Long nextCursor,
        boolean hasNext,
        int size
) {
}
