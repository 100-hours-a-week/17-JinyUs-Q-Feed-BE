package com.ktb.question.domain;

import lombok.Getter;

/**
 * 질문 종류 Enum
 */
@Getter
public enum QuestionType {
    CS("CS"),
    SYSTEM_DESIGN("SYSTEM_DESIGN"),
    PORTFOLIO("PORTFOLIO");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }
}
