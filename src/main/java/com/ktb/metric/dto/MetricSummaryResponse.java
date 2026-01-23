package com.ktb.metric.dto;

public record MetricSummaryResponse(
        Long metricId,
        String name,
        String description,
        boolean useYn
) {
}
