package com.ktb.redis.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {

    DAILY_RECOMMENDATION_ANSWERED("question:daily-recommendation:answered:%d:%s");

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
