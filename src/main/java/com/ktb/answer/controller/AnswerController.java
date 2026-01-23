package com.ktb.answer.controller;

import com.ktb.answer.dto.AnswerSubmitCommand;
import com.ktb.answer.dto.AnswerSubmitResult;
import com.ktb.answer.dto.request.AnswerDetailRequest;
import com.ktb.answer.dto.request.AnswerListRequest;
import com.ktb.answer.dto.request.AnswerSubmitRequest;
import com.ktb.answer.dto.request.SessionAnswerSubmitRequest;
import com.ktb.answer.dto.response.AnswerDetailResponse;
import com.ktb.answer.dto.response.AnswerListResponse;
import com.ktb.answer.dto.response.AnswerSubmitResponse;
import com.ktb.answer.dto.response.FeedbackResponse;
import com.ktb.answer.dto.response.SessionAnswerSubmitResponse;
import com.ktb.answer.service.AiFeedbackOrchestrator;
import com.ktb.answer.service.AnswerApplicationService;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import com.ktb.common.dto.ApiResponse;
import com.ktb.swagger.answer.AnswerApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnswerController implements AnswerApi {

    private final AnswerApplicationService answerApplicationService;
    private final AiFeedbackOrchestrator aiFeedbackOrchestrator;

    private static final String MESSAGE_ANSWER_LIST_RETRIEVED = "learning_records_retrieval_success";
    private static final String MESSAGE_ANSWER_DETAIL_RETRIEVED = "record_retrieval_success";
    private static final String MESSAGE_ANSWER_SUBMITTED = "answer_submitted_success";
    private static final String MESSAGE_FEEDBACK_RETRIEVED = "feedback_retrieval_success";

    @GetMapping("/answers")
    @Override
    public ResponseEntity<ApiResponse<AnswerListResponse>> getAnswerList(
            Long accountId,  // TODO: Spring Security에서 주입
            @Valid @ModelAttribute AnswerListRequest request
    ) {
        // TODO: 구현 필요
        // 1. accountId 추출 (Spring Security Authentication)
        // 2. Request DTO를 Command로 변환
        // 3. Service 호출
        // 4. Result를 Response DTO로 변환
        // 5. ApiResponse로 래핑하여 반환

        log.info("GET /api/answers - accountId: {}", accountId);

        // TODO: 임시 구현 (Service 연동 필요)
        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_ANSWER_LIST_RETRIEVED, null)
        );
    }

    @GetMapping("/answers/{answerId}")
    @Override
    public ResponseEntity<ApiResponse<AnswerDetailResponse>> getAnswerDetail(
            Long accountId,  // TODO: Spring Security에서 주입
            @PathVariable Long answerId,
            @Valid @ModelAttribute AnswerDetailRequest request
    ) {
        // TODO: 구현 필요
        // 1. accountId 추출
        // 2. Service 호출 (소유권 검증 포함)
        // 3. expand 파라미터 처리
        // 4. Result를 Response DTO로 변환
        // 5. ApiResponse로 래핑하여 반환

        log.info("GET /api/answers/{} - accountId: {}", answerId, accountId);

        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_ANSWER_DETAIL_RETRIEVED, null)
        );
    }

    @PostMapping(value = "/interview/answers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<ApiResponse<AnswerSubmitResponse>> submitAnswer(
            @AuthenticationPrincipal SecurityUserAccount principal,
            @Valid @ModelAttribute AnswerSubmitRequest request
    ) {
        Long accountId = principal.getAccount().getId();
        AnswerSubmitCommand command = new AnswerSubmitCommand(request.questionId(), request.answerText(), request.answerType());

        AnswerSubmitResult submitResult = answerApplicationService.submit(accountId, command);

        log.info("POST /api/interview/answers - accountId: {}, questionId: {}",
                accountId, request.questionId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(MESSAGE_ANSWER_SUBMITTED, submitResult.from()));
    }

    @PostMapping(value = "/interview/sessions/{sessionId}/answers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<ApiResponse<SessionAnswerSubmitResponse>> submitSessionAnswer(
            Long accountId,  // TODO: Spring Security에서 주입
            @PathVariable String sessionId,
            @Valid @ModelAttribute SessionAnswerSubmitRequest request
    ) {
        // TODO: 구현 필요
        // 1. accountId 추출
        // 2. 세션 소유권 검증
        // 3. Request DTO를 Command로 변환
        // 4. Service 호출 (파일 업로드, 즉각 피드백, 세션 업데이트, 다음 질문 생성)
        // 5. Result를 Response DTO로 변환
        // 6. ApiResponse로 래핑하여 201 Created 반환

        log.info("POST /api/interview/sessions/{}/answers - accountId: {}, questionId: {}",
                sessionId, accountId, request.questionId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(MESSAGE_ANSWER_SUBMITTED, null));
    }

    @GetMapping("/interviews/answers/{answerId}/feedback")
    @Override
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @AuthenticationPrincipal SecurityUserAccount principal,
            @PathVariable Long answerId
    ) {
        Long accountId = principal.getAccount().getId();
        FeedbackResponse feedbackResponse = aiFeedbackOrchestrator.getFeedbackSync(answerId, accountId);

        log.info("GET /api/interviews/answers/{}/feedback - accountId: {}", answerId, accountId);

        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_FEEDBACK_RETRIEVED, feedbackResponse)
        );
    }
}
