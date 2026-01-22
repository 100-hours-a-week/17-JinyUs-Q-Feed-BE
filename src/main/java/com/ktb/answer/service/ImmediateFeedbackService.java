package com.ktb.answer.service;

import com.ktb.answer.dto.KeywordCheckResult;
import com.ktb.answer.dto.response.AnswerSubmitResponse.ImmediateFeedback;
import com.ktb.hashtag.domain.Hashtag;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ImmediateFeedbackService {
    /**
     * 즉각 피드백 생성 (동기 처리)
     * @param questionId 질문 ID
     * @param answerText 답변 텍스트
     * @return 키워드 체크리스트 기반 즉각 피드백
     */
    ImmediateFeedback evaluate(Long questionId, String answerText);

    /**
     * 핵심 키워드 추출
     * @return 질문별 핵심 키워드 목록
     */
    List<Hashtag> extractKeywords(Long questionId);

    /**
     * 키워드 포함 여부 체크
     * @return 키워드별 포함 여부 결과
     */
    List<KeywordCheckResult> checkKeywords(String answerText, List<Hashtag> keywords);
}
