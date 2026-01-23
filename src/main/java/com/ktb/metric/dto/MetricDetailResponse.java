package com.ktb.metric.dto;

import java.time.LocalDateTime;

public record MetricDetailResponse(
        Long metricId,
        String name,
        String description,
        boolean useYn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
