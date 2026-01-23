package com.ktb.metric.dto;

import java.util.List;

public record MetricListResponse(
        List<MetricSummaryResponse> metrics,
        MetricPaginationResponse pagination
) {
}
