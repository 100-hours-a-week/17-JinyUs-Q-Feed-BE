package com.ktb.answer.service.impl;

import com.ktb.answer.dto.ImmediateFeedbackResult;
import com.ktb.answer.dto.KeywordCheckResult;
import com.ktb.answer.dto.response.AnswerSubmitResponse.ImmediateFeedback;
import com.ktb.answer.service.ImmediateFeedbackService;
import com.ktb.hashtag.domain.Hashtag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImmediateFeedbackServiceImpl implements ImmediateFeedbackService {

    private static final List<String> DEFAULT_KEYWORDS = List.of(
            "프로세스", "스레드", "메모리", "CPU", "동기화"
    );

    @Override
    public ImmediateFeedback evaluate(Long questionId, String answerText) {
        // TODO: 구현 필요
        // 1. questionId로 질문별 핵심 키워드 조회 (현재는 하드코딩)
        // 2. answerText에서 키워드 포함 여부 체크
        // 3. KeywordCheckResult 리스트 생성
        // 4. ImmediateFeedbackResult 반환

        log.debug("Evaluating immediate feedback for questionId: {}", questionId);

        List<KeywordCheckResult> keywordChecks = new ArrayList<>();

        for (String keyword : DEFAULT_KEYWORDS) {
            boolean included = answerText != null && answerText.contains(keyword);
            keywordChecks.add(new KeywordCheckResult(keyword, included));
        }

        return new ImmediateFeedback(keywordChecks);
    }

    @Override
    public List<Hashtag> extractKeywords(Long questionId) {
        // TODO: 구현 필요
        // 1. questionId로 질문별 핵심 키워드 조회
        // 2. QUESTION_HASHTAG 조인 또는 별도 키워드 테이블 조회
        // 3. Hashtag 리스트 반환
        // 현재는 빈 리스트 반환 (추후 Question-Hashtag 연관 구현 후 활성화)

        log.debug("Extracting keywords for questionId: {}", questionId);
        return List.of();
    }

    @Override
    public List<KeywordCheckResult> checkKeywords(String answerText, List<Hashtag> keywords) {
        // TODO: 구현 필요
        // 1. answerText에서 각 keyword 포함 여부 체크
        // 2. 대소문자 무시 또는 형태소 분석 적용 (선택)
        // 3. KeywordCheckResult 리스트 생성 및 반환

        log.debug("Checking keywords in answer text: keywordCount={}", keywords.size());

        List<KeywordCheckResult> results = new ArrayList<>();
        for (Hashtag hashtag : keywords) {
            boolean included = answerText != null && answerText.contains(hashtag.getName());
            results.add(new KeywordCheckResult(hashtag.getName(), included));
        }

        return results;
    }
}
