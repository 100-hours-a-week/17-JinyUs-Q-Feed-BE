package com.ktb.metric.controller;

import com.ktb.common.dto.ApiResponse;
import com.ktb.metric.dto.MetricCreateRequest;
import com.ktb.metric.dto.MetricDetailResponse;
import com.ktb.metric.dto.MetricListResponse;
import com.ktb.metric.dto.MetricUpdateRequest;
import com.ktb.metric.service.MetricService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Validated
public class MetricController {

    private final MetricService metricService;

    private static final String MESSAGE_METRIC_LIST_RETRIEVED = "metrics_retrieval_success";
    private static final String MESSAGE_METRIC_DETAIL_RETRIEVED = "metric_retrieval_success";
    private static final String MESSAGE_METRIC_CREATED = "metric_created_success";
    private static final String MESSAGE_METRIC_UPDATED = "metric_updated_success";
    private static final String MESSAGE_METRIC_DELETED = "metric_deleted_success";

    /**
     * 평가 지표 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<MetricListResponse>> getMetrics(
            @RequestParam(required = false) Boolean useYn,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        MetricListResponse result = metricService.getMetrics(useYn, cursor, size);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_LIST_RETRIEVED, result));
    }

    /**
     * 평가 지표 상세 조회
     */
    @GetMapping("/{metricId}")
    public ResponseEntity<ApiResponse<MetricDetailResponse>> getMetric(
            @PathVariable Long metricId
    ) {
        MetricDetailResponse result = metricService.getMetric(metricId);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_DETAIL_RETRIEVED, result));
    }

    /**
     * 평가 지표 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MetricDetailResponse>> createMetric(
            @Valid @RequestBody MetricCreateRequest request
    ) {
        MetricDetailResponse result = metricService.createMetric(request);
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(MESSAGE_METRIC_CREATED, result));
    }

    /**
     * 평가 지표 수정
     */
    @PatchMapping("/{metricId}")
    public ResponseEntity<ApiResponse<MetricDetailResponse>> updateMetric(
            @PathVariable Long metricId,
            @Valid @RequestBody MetricUpdateRequest request
    ) {
        MetricDetailResponse result = metricService.updateMetric(metricId, request);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_UPDATED, result));
    }

    /**
     * 평가 지표 삭제 (비활성화)
     */
    @DeleteMapping("/{metricId}")
    public ResponseEntity<ApiResponse<Void>> deleteMetric(
            @PathVariable Long metricId
    ) {
        metricService.deleteMetric(metricId);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_DELETED, null));
    }
}
