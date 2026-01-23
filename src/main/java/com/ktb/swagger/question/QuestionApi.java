package com.ktb.swagger.question;

import com.ktb.common.dto.ApiResponse;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import com.ktb.question.dto.QuestionCreateRequest;
import com.ktb.question.dto.QuestionDetailResponse;
import com.ktb.question.dto.QuestionKeywordCheckRequest;
import com.ktb.question.dto.QuestionKeywordCheckResponse;
import com.ktb.question.dto.QuestionKeywordListResponse;
import com.ktb.question.dto.QuestionListResponse;
import com.ktb.question.dto.QuestionSearchResponse;
import com.ktb.question.dto.QuestionUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Question API", description = "질문 관리 API")
public interface QuestionApi {

    @Operation(summary = "질문 목록 조회", description = "질문 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionListResponse>> getQuestions(
            @Parameter(description = "카테고리") QuestionCategory category,
            @Parameter(description = "질문 유형") QuestionType type,
            @Parameter(description = "커서") Long cursor,
            @Parameter(description = "사이즈", example = "10") int size
    );

    @Operation(summary = "질문 상세 조회", description = "질문 상세를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(
            @Parameter(description = "질문 ID", example = "1") Long questionId
    );

    @Operation(summary = "질문 검색", description = "키워드로 질문을 검색합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionSearchResponse>> searchQuestions(
            @Parameter(description = "검색어") String keyword,
            @Parameter(description = "카테고리") QuestionCategory category,
            @Parameter(description = "질문 유형") QuestionType type,
            @Parameter(description = "커서") Long cursor,
            @Parameter(description = "사이즈", example = "10") int size
    );

    @Operation(summary = "질문 생성", description = "새 질문을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionDetailResponse>> createQuestion(QuestionCreateRequest request);

    @Operation(summary = "질문 수정", description = "질문을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionDetailResponse>> updateQuestion(
            @Parameter(description = "질문 ID", example = "1") Long questionId,
            QuestionUpdateRequest request
    );

    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "질문 ID", example = "1") Long questionId
    );

    @Operation(summary = "질문 키워드 조회", description = "질문 키워드 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionKeywordListResponse>> getQuestionKeywords(
            @Parameter(description = "질문 ID", example = "1") Long questionId
    );

    @Operation(summary = "질문 키워드 체크", description = "질문 키워드 포함 여부를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "질문 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<QuestionKeywordCheckResponse>> checkQuestionKeyword(
            @Parameter(description = "질문 ID", example = "1") Long questionId,
            QuestionKeywordCheckRequest request
    );
}
