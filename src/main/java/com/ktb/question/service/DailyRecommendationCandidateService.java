package com.ktb.question.service;

import java.time.LocalDate;
import java.util.List;

public interface DailyRecommendationCandidateService {

    List<Long> getCandidateIds(LocalDate date);

    boolean isCandidateToday(Long questionId);

    List<Long> getFreshCandidateIds();
}
