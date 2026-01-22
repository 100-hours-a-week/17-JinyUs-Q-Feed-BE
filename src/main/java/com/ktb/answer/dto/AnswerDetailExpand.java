package com.ktb.answer.dto;

import com.ktb.answer.exception.InvalidExpandException;
import java.util.Locale;

public enum AnswerDetailExpand {
    QUESTION,
    FEEDBACK,
    IMMEDIATE_FEEDBACK;

    public static AnswerDetailExpand from(String value) {
        if (value == null) {
            throw new InvalidExpandException(null);
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (AnswerDetailExpand expand : values()) {
            if (expand.name().equals(normalized)) {
                return expand;
            }
        }
        throw new InvalidExpandException(value);
    }
}
