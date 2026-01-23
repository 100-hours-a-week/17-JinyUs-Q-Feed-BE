package com.ktb.metric.dto;

public record MetricPaginationResponse(
        Long nextCursor,
        boolean hasNext,
        int size
) {
}
