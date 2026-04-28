package com.ktb.question.service.impl;

import com.ktb.question.repository.QuestionRepository;
import com.ktb.question.service.DailyRecommendationCandidateService;
import com.ktb.question.service.DailyRecommendationProperties;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(DailyRecommendationProperties.class)
public class DailyRecommendationCandidateServiceImpl implements DailyRecommendationCandidateService {

    private final QuestionRepository questionRepository;
    private final DailyRecommendationProperties properties;
    private final DailyRecommendationCandidateCache candidateCache;

    @Override
    public List<Long> getCandidateIds(LocalDate date) {
        return candidateCache.getCandidateIds(date);
    }

    @Override
    public boolean isCandidateToday(Long questionId) {
        return candidateCache.getCandidateIds(LocalDate.now()).contains(questionId);
    }

    @Override
    public List<Long> getFreshCandidateIds() {
        return questionRepository.findRandomActiveCandidateIds(properties.getCandidateCount());
    }
}
