package com.ktb.answer.dto;

import java.util.Collections;
import java.util.List;

public record ImmediateFeedbackResult(List<KeywordCheckResult> keywords) {

    public ImmediateFeedbackResult {
        keywords = keywords == null ? Collections.emptyList() : List.copyOf(keywords);
    }
}
