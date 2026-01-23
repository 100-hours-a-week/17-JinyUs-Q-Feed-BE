package com.ktb.metric.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MetricCreateRequest(
        @NotBlank
        @Size(max = 30)
        String name,
        @Size(max = 255)
        String description
) {
}
