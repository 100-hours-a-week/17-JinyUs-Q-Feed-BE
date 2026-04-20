package com.ktb.ai.feedback.service;

import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;
import com.ktb.answer.domain.AnswerType;
import com.ktb.common.dto.ApiResponse;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;

public interface AiFeedbackService {

    /**
     * AI 피드백 동기 평가
     *
     * @param userId          사용자 ID
     * @param questionId      질문 ID
     * @param type            질문 타입
     * @param category        질문 카테고리
     * @param answerType      인터뷰 카테고리
     * @param questionContent 질문 내용
     * @param answerContent   답변 내용
     * @return AI 피드백 응답
     * @throws com.ktb.ai.feedback.exception.AiFeedbackServiceException FastAPI 서버 호출 실패 시
     */
    ApiResponse<AiFeedbackResponse> evaluateSync(
            Long userId,
            Long questionId,
            QuestionType type,
            QuestionCategory category,
            AnswerType answerType,
            String questionContent,
            String answerContent
    );

}
