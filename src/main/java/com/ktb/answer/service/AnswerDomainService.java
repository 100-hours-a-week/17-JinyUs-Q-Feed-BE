package com.ktb.answer.service;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.DuplicateAnswerException;
import com.ktb.answer.exception.InvalidAnswerContentException;
import com.ktb.answer.exception.InvalidAnswerStatusTransitionException;
import org.springframework.stereotype.Service;

@Service
public interface AnswerDomainService {
    /**
     * 답변 생성
     * @return 생성된 Answer 엔티티
     */
    public Answer createAnswer(Long accountId, Long questionId, String answerContent,
                               String audioUrl, String videoUrl, AnswerType type);

    /**
     * 답변 소유권 검증
     * @throws AnswerAccessDeniedException 본인 답변이 아닌 경우
     */
    public void validateOwnership(Answer answer, Long accountId)
        throws AnswerAccessDeniedException;

    /**
     * 답변 상태 전이
     * @throws InvalidAnswerStatusTransitionException 허용되지 않는 상태 전이인 경우
     */
    public void transitionStatus(Answer answer, AnswerStatus nextStatus)
        throws InvalidAnswerStatusTransitionException;

    /**
     * 중복 답변 검증 (세션 내 동일 질문)
     * @throws DuplicateAnswerException 이미 답변이 존재하는 경우
     */
    public void checkDuplicateAnswer(String sessionId, Long questionId)
        throws DuplicateAnswerException;

    /**
     * 답변 텍스트 유효성 검증
     * @throws InvalidAnswerContentException 답변 내용이 유효하지 않은 경우
     */
    public void validateAnswerContent(String answerText, String audioUrl)
        throws InvalidAnswerContentException;
}
