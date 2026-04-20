package com.ktb.answer.dto;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record AnswerDetailQuery(Set<AnswerDetailExpand> expands) {

    public AnswerDetailQuery {
        if (expands == null || expands.isEmpty()) {
            expands = EnumSet.noneOf(AnswerDetailExpand.class);
        } else {
            expands = EnumSet.copyOf(expands);
        }
    }

    public boolean includeQuestion() {
        return expands.contains(AnswerDetailExpand.QUESTION);
    }

    public boolean includeFeedback() {
        return expands.contains(AnswerDetailExpand.FEEDBACK);
    }

    public boolean includeImmediateFeedback() {
        return expands.contains(AnswerDetailExpand.IMMEDIATE_FEEDBACK);
    }

    public static AnswerDetailQuery of(Iterable<String> values) {
        if (values == null) {
            return new AnswerDetailQuery(Collections.emptySet());
        }
        EnumSet<AnswerDetailExpand> resolved = EnumSet.noneOf(AnswerDetailExpand.class);
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            resolved.add(AnswerDetailExpand.from(value));
        }
        return new AnswerDetailQuery(resolved);
    }

    public static AnswerDetailQuery empty() {
        return new AnswerDetailQuery(Collections.emptySet());
    }
}
