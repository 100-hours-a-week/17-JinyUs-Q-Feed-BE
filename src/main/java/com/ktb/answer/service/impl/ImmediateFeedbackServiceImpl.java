package com.ktb.answer.service.impl;

import com.ktb.answer.dto.ImmediateFeedbackResult;
import com.ktb.answer.dto.KeywordCheckResult;
import com.ktb.answer.service.ImmediateFeedbackService;
import com.ktb.hashtag.domain.QuestionHashtag;
import com.ktb.hashtag.repository.QuestionHashtagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImmediateFeedbackServiceImpl implements ImmediateFeedbackService {

    private final QuestionHashtagRepository questionHashtagRepository;

    @Override
    public ImmediateFeedbackResult evaluate(Long questionId, String answerText) {
        log.debug("Evaluating immediate feedback - questionId: {}", questionId);

        List<QuestionHashtag> questionHashtags = extractKeywords(questionId);
        List<KeywordCheckResult> keywordChecks = checkKeywords(answerText, questionHashtags);

        return new ImmediateFeedbackResult(keywordChecks);
    }

    @Override
    public List<QuestionHashtag> extractKeywords(Long questionId) {
        List<QuestionHashtag> questionHashtags =
            questionHashtagRepository.findKeywordNamesByQuestionId(questionId);

        log.debug("Extracting keywords for questionId: {}", questionId);
        return questionHashtags;
    }

    @Override
    public List<KeywordCheckResult> checkKeywords(String answerText, List<QuestionHashtag> questionHashtags) {
        log.debug("Checking keywords in answer text: keywordCount={}", questionHashtags.size());

        String normalizedAnswerText = answerText.toLowerCase();

        List<KeywordCheckResult> keywordChecks = questionHashtags.stream()
            .map(qh -> {
                String keyword = qh.getHashtag().getName();
                boolean included = normalizedAnswerText.contains(keyword);

                return new KeywordCheckResult(
                    qh.getHashtag().getId(),
                    keyword,
                    included
                );
            })
            .toList();

        log.debug("Immediate feedback - total keywords: {}, included: {}",
            keywordChecks.size(),
            keywordChecks.stream().filter(KeywordCheckResult::included).count());

        return keywordChecks;
    }
}
