package com.ktb.answer.dto;

public record KeywordCheckResult(
        Long keywordId,
        String keyword,
        boolean included
) {
}
