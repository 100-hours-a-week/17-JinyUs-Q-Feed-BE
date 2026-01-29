package com.ktb.question.domain;

import lombok.Getter;

/**
 * 질문 카테코리 ENUM
 */
@Getter
public enum QuestionCategory {
    OS("운영체제"),
    NETWORK("네트워크"),
    DB("데이터베이스"),
    COMPUTER_ARCHITECTURE("컴퓨터 구조"),
    DATA_STRUCTURE_ALGORITHM("자료구조&알고리즘");

    private final String category;

    QuestionCategory(String category) {
        this.category = category;
    }
}
