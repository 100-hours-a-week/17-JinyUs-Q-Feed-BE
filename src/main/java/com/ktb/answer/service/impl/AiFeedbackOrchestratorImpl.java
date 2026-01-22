package com.ktb.answer.service.impl;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.dto.FeedbackStatus;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AiFeedbackOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiFeedbackOrchestratorImpl implements AiFeedbackOrchestrator {

    private final AnswerRepository answerRepository;

    @Override
    public void enqueue(Long answerId) {
        // TODO: MVP V2 구현 필요
        // 1. Kafka Producer 구성
        // 2. ANSWER_AI_FEEDBACK_REQUESTED 이벤트 발행
        // 3. 이벤트 payload: { answerId, questionId, answerContent }
        // 4. 발행 실패 시 예외 처리 (재시도 정책)

        log.info("Enqueueing AI feedback request for answerId: {}", answerId);

        // TODO: Kafka 연동 후 활성화
        // kafkaTemplate.send("answer-feedback-requests", answerId.toString(), event);

        log.warn("AI feedback enqueue not implemented yet (Kafka integration required)");
    }

    @Override
    public FeedbackStatus getStatus(Long answerId) {
        // TODO: 구현 필요
        // 1. Answer 조회
        // 2. Answer.status를 FeedbackStatus로 매핑
        //    - AI_FEEDBACK_PROCESSING -> PROCESSING
        //    - COMPLETED -> COMPLETED
        //    - FAILED -> FAILED
        //    - FAILED_RETRYABLE -> FAILED_RETRYABLE
        // 3. FeedbackStatus 반환

        log.debug("Getting feedback status for answerId: {}", answerId);

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        return mapAnswerStatusToFeedbackStatus(answer.getStatus());
    }

    @Override
    public void requestRetry(Long answerId) {
        // TODO: MVP V2 구현 필요
        // 1. Answer 조회
        // 2. 상태 검증 (FAILED_RETRYABLE만 허용)
        // 3. 재시도 횟수 체크 (최대 3회)
        // 4. Kafka 이벤트 재발행
        // 5. Answer 상태를 AI_FEEDBACK_PROCESSING으로 전이

        log.info("Requesting feedback retry for answerId: {}", answerId);

        // TODO: 재시도 정책 구현 후 활성화
        throw new UnsupportedOperationException("Feedback retry not yet implemented");
    }

    private FeedbackStatus mapAnswerStatusToFeedbackStatus(AnswerStatus answerStatus) {
        return switch (answerStatus) {
            case AI_FEEDBACK_PROCESSING -> FeedbackStatus.PROCESSING;
            case COMPLETED -> FeedbackStatus.COMPLETED;
            case FAILED -> FeedbackStatus.FAILED;
            case FAILED_RETRYABLE -> FeedbackStatus.FAILED_RETRYABLE;
            default -> FeedbackStatus.NOT_AVAILABLE;
        };
    }
}
