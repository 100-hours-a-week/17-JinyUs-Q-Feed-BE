package com.ktb.answer.dto;

import com.ktb.answer.dto.response.AnswerSubmitResponse.KeywordCheck;
import java.util.List;
import java.util.stream.Collectors;

public record KeywordCheckResult(
        String keyword,
        boolean included
) {

    public static List<KeywordCheckResult> from(List<KeywordCheck> keywordChecks) {
        return keywordChecks.stream()
            .map(keywordCheck ->
                new KeywordCheckResult(keywordCheck.keyword(), keywordCheck.included()))
            .collect(Collectors.toList());
    }
}
