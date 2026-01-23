package com.ktb.swagger.metric;

import com.ktb.common.dto.ApiResponse;
import com.ktb.metric.dto.MetricCreateRequest;
import com.ktb.metric.dto.MetricDetailResponse;
import com.ktb.metric.dto.MetricListResponse;
import com.ktb.metric.dto.MetricUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Metric API", description = "평가 지표 관리 API")
public interface MetricApi {

    @Operation(summary = "평가 지표 목록 조회", description = "평가 지표 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<MetricListResponse>> getMetrics(
            @Parameter(description = "활성 여부") Boolean useYn,
            @Parameter(description = "커서") Long cursor,
            @Parameter(description = "사이즈", example = "10") int size
    );

    @Operation(summary = "평가 지표 상세 조회", description = "평가 지표 상세를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<MetricDetailResponse>> getMetric(
            @Parameter(description = "평가 지표 ID", example = "1") Long metricId
    );

    @Operation(summary = "평가 지표 생성", description = "새 평가 지표를 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<MetricDetailResponse>> createMetric(MetricCreateRequest request);

    @Operation(summary = "평가 지표 수정", description = "평가 지표를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<MetricDetailResponse>> updateMetric(
            @Parameter(description = "평가 지표 ID", example = "1") Long metricId,
            MetricUpdateRequest request
    );

    @Operation(summary = "평가 지표 삭제", description = "평가 지표를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 지표 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> deleteMetric(
            @Parameter(description = "평가 지표 ID", example = "1") Long metricId
    );
}
