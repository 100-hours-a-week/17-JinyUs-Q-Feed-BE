package com.ktb.question.dto;

public record KeywordMatchResponse(
        String keyword,
        boolean included
) {
}
