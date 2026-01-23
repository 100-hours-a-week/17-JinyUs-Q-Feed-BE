package com.ktb.metric.dto;

import jakarta.validation.constraints.Size;

public record MetricUpdateRequest(
        @Size(max = 30)
        String name,
        @Size(max = 255)
        String description,
        Boolean useYn
) {
}
