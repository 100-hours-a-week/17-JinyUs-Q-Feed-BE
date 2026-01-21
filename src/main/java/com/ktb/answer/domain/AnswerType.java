package com.ktb.answer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnswerType {

    PRACTICE_INTERVIEW("연습 면접"),

    REAL_INTERVIEW("실전 면접"),

    PORTFOLIO_INTERVIEW("포트폴리오 면접");

    private final String description;
}
