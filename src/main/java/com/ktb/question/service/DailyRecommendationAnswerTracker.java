package com.ktb.question.service;

import java.time.LocalDate;
import java.util.Set;

public interface DailyRecommendationAnswerTracker {

    Set<Long> getAnsweredIds(Long accountId, LocalDate date);

    void recordAnswer(Long accountId, Long questionId, LocalDate date);
}
