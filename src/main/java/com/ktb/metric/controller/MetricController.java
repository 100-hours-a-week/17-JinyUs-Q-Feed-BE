package com.ktb.metric.controller;

import com.ktb.common.dto.ApiResponse;
import com.ktb.metric.dto.MetricCreateRequest;
import com.ktb.metric.dto.MetricDetailResponse;
import com.ktb.metric.dto.MetricListResponse;
import com.ktb.metric.dto.MetricUpdateRequest;
import com.ktb.metric.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Metric API", description = "평가 지표 관리 API")
@RestController
@RequestMapping("/api/metrics")
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
    @Operation(summary = "평가 지표 목록 조회", description = "평가 지표 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<MetricListResponse>> getMetrics(
            @Parameter(description = "활성 여부") @RequestParam(required = false) Boolean useYn,
            @Parameter(description = "커서") @RequestParam(required = false) Long cursor,
            @Parameter(description = "사이즈", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        MetricListResponse result = metricService.getMetrics(useYn, cursor, size);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_LIST_RETRIEVED, result));
    }

    /**
     * 평가 지표 상세 조회
     */
    @GetMapping("/{metricId}")
    @Operation(summary = "평가 지표 상세 조회", description = "평가 지표 상세를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<MetricDetailResponse>> getMetric(
            @Parameter(description = "평가 지표 ID", example = "1")
            @PathVariable Long metricId
    ) {
        MetricDetailResponse result = metricService.getMetric(metricId);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_DETAIL_RETRIEVED, result));
    }

    /**
     * 평가 지표 생성
     */
    @PostMapping
    @Operation(summary = "평가 지표 생성", description = "새 평가 지표를 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
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
    @Operation(summary = "평가 지표 수정", description = "평가 지표를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<MetricDetailResponse>> updateMetric(
            @Parameter(description = "평가 지표 ID", example = "1")
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
    @Operation(summary = "평가 지표 삭제", description = "평가 지표를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteMetric(
            @Parameter(description = "평가 지표 ID", example = "1")
            @PathVariable Long metricId
    ) {
        metricService.deleteMetric(metricId);
        return ResponseEntity.ok(new ApiResponse<>(MESSAGE_METRIC_DELETED, null));
    }
}
