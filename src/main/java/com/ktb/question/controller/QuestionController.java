package com.ktb.question.controller;

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
import com.ktb.question.service.QuestionService;
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
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Validated
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<QuestionListResponse>> getQuestions(
            @RequestParam(required = false) QuestionCategory category,
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        QuestionListResponse result = questionService.getQuestions(category, type, cursor, size);
        return ResponseEntity.ok(new ApiResponse<>("questions_retrieval_success", result));
    }

    /**
     * 질문 상세 조회
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(
            @PathVariable Long questionId
    ) {
        QuestionDetailResponse result = questionService.getQuestionDetail(questionId);
        return ResponseEntity.ok(new ApiResponse<>("question_retrieval_success", result));
    }

    /**
     * 질문 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<QuestionSearchResponse>> searchQuestions(
            @RequestParam(name = "q") String keyword,
            @RequestParam(name = "category", required = false) QuestionCategory category,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        QuestionSearchResponse result = questionService.search(keyword, category, type, cursor, size);
        return ResponseEntity.ok(new ApiResponse<>("search_success", result));
    }

    /**
     * 질문 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request
    ) {
        QuestionDetailResponse result = questionService.createQuestion(request);
        return ResponseEntity.status(201)
                .body(new ApiResponse<>("question_created_success", result));
    }

    /**
     * 질문 수정
     */
    @PatchMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest request
    ) {
        QuestionDetailResponse result = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(new ApiResponse<>("question_updated_success", result));
    }

    /**
     * 질문 삭제 (Soft Delete)
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(new ApiResponse<>("question_deleted_success", null));
    }

    /**
     * 질문 핵심 키워드 조회
     */
    @GetMapping("/{questionId}/keywords")
    public ResponseEntity<ApiResponse<QuestionKeywordListResponse>> getQuestionKeywords(
            @PathVariable Long questionId
    ) {
        QuestionKeywordListResponse result = questionService.getQuestionKeywords(questionId);
        return ResponseEntity.ok(new ApiResponse<>("question_keywords_retrieval_success", result));
    }

    /**
     * 질문 핵심 키워드 포함 여부 확인
     */
    @PostMapping("/{questionId}/keyword-checks")
    public ResponseEntity<ApiResponse<QuestionKeywordCheckResponse>> checkQuestionKeyword(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionKeywordCheckRequest request
    ) {
        QuestionKeywordCheckResponse result = questionService.checkQuestionKeywords(questionId, request.keywords());
        return ResponseEntity.ok(new ApiResponse<>("question_keywords_check_success", result));
    }
}
