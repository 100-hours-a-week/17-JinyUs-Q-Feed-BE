package com.ktb.redis.policy;

import com.ktb.redis.constant.CacheNames;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CachePolicy {
    QUESTION_CATEGORIES(CacheNames.QUESTION_CATEGORIES, CacheTtl.LONG,   false),
    QUESTION_TYPES     (CacheNames.QUESTION_TYPES,      CacheTtl.LONG,   false),
    QUESTION_LIST      (CacheNames.QUESTION_LIST,       CacheTtl.MEDIUM, false),
    QUESTION_DETAIL    (CacheNames.QUESTION_DETAIL,     CacheTtl.LONG,   false),
    QUESTION_KEYWORDS  (CacheNames.QUESTION_KEYWORDS,   CacheTtl.LONG,   false),
    QUESTION_DAILY_REC (CacheNames.QUESTION_DAILY_RECOMMENDATION, CacheTtl.LONG, true),
    METRIC_LIST        (CacheNames.METRIC_LIST,         CacheTtl.MEDIUM, false),
    METRIC_DETAIL      (CacheNames.METRIC_DETAIL,       CacheTtl.LONG,   false);

    private final String cacheName;
    private final CacheTtl ttl;
    private final boolean hotKey;
}
