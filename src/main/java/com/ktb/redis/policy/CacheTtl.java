package com.ktb.redis.policy;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheTtl {
    SHORT(Duration.ofMinutes(1)),
    MEDIUM(Duration.ofMinutes(10)),
    LONG(Duration.ofHours(1)),
    DAILY(Duration.ofDays(1));

    private final Duration duration;
}
