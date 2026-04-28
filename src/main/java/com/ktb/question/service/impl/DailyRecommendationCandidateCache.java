package com.ktb.question.service.impl;

import com.ktb.question.repository.QuestionRepository;
import com.ktb.question.service.DailyRecommendationProperties;
import com.ktb.redis.constant.CacheNames;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DailyRecommendationCandidateCache {

    private final QuestionRepository questionRepository;
    private final DailyRecommendationProperties properties;

    @Cacheable(cacheNames = CacheNames.QUESTION_DAILY_RECOMMENDATION_CANDIDATES,
               key = "#date.format(T(java.time.format.DateTimeFormatter).BASIC_ISO_DATE)")
    public List<Long> getCandidateIds(LocalDate date) {
        return questionRepository.findRandomActiveCandidateIds(properties.getCandidateCount());
    }
}
