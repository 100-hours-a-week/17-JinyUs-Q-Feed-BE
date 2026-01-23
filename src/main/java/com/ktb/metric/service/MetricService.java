package com.ktb.metric.service;

import com.ktb.metric.dto.MetricCreateRequest;
import com.ktb.metric.dto.MetricDetailResponse;
import com.ktb.metric.dto.MetricListResponse;
import com.ktb.metric.dto.MetricUpdateRequest;

public interface MetricService {
    MetricListResponse getMetrics(Boolean useYn, Long cursor, int size);

    MetricDetailResponse getMetric(Long metricId);

    MetricDetailResponse createMetric(MetricCreateRequest request);

    MetricDetailResponse updateMetric(Long metricId, MetricUpdateRequest request);

    void deleteMetric(Long metricId);
}
