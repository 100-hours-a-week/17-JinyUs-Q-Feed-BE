package com.ktb.swagger.answer;

import com.ktb.answer.dto.request.AnswerDetailRequest;
import com.ktb.answer.dto.request.AnswerListRequest;
import com.ktb.answer.dto.request.AnswerSubmitRequest;
import com.ktb.answer.dto.request.SessionAnswerSubmitRequest;
import com.ktb.answer.dto.response.AnswerDetailResponse;
import com.ktb.answer.dto.response.AnswerListResponse;
import com.ktb.answer.dto.response.AnswerSubmitResponse;
import com.ktb.answer.dto.response.FeedbackResponse;
import com.ktb.answer.dto.response.SessionAnswerSubmitResponse;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import com.ktb.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Answer API", description = "답변 관리 API")
public interface AnswerApi {

    @Operation(summary = "답변 목록 조회", description = "사용자의 학습 기록 목록을 조회합니다 (본인만)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    ResponseEntity<ApiResponse<AnswerListResponse>> getAnswerList(
            @Parameter(hidden = true) Long accountId,
            AnswerListRequest request
    );

    @Operation(summary = "답변 상세 조회", description = "특정 답변의 상세 정보를 조회합니다 (본인만)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<AnswerDetailResponse>> getAnswerDetail(
            @Parameter(hidden = true) Long accountId,
            @Parameter(description = "답변 ID", example = "1") Long answerId,
            AnswerDetailRequest request
    );

    @Operation(summary = "답변 제출 (연습/단일)", description = "텍스트답변을 제출합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "제출 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<AnswerSubmitResponse>> submitAnswer(
            @Parameter(hidden = true) SecurityUserAccount principal,
            AnswerSubmitRequest request
    );

    @Operation(summary = "실전 세션 기반 답변 제출", description = "실전 모드 세션에서 비디오 답변을 제출합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "제출 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "세션 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "세션 상태 오류 또는 중복 답변")
    })
    ResponseEntity<ApiResponse<SessionAnswerSubmitResponse>> submitSessionAnswer(
            @Parameter(hidden = true) Long accountId,
            @Parameter(description = "세션 ID", example = "uuid-string") String sessionId,
            SessionAnswerSubmitRequest request
    );

    @Operation(summary = "AI 피드백 조회", description = "답변에 대한 AI 피드백을 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피드백 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "피드백 처리 중"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @Parameter(hidden = true) SecurityUserAccount principal,
            @Parameter(description = "답변 ID", example = "1") Long answerId
    );
}
