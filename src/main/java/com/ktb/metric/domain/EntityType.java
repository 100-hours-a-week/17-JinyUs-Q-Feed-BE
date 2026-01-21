package com.ktb.metric.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 메트릭 대상 엔티티 타입
 */
@Getter
@RequiredArgsConstructor
public enum EntityType {

    QUESTION("질문"),
    ANSWER("답변");

    private final String description;
}
