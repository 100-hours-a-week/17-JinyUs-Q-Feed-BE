package com.ktb.question.service.impl;

import com.ktb.question.service.DailyRecommendationAnswerTracker;
import com.ktb.redis.constant.RedisKey;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDailyRecommendationAnswerTracker implements DailyRecommendationAnswerTracker {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final StringRedisTemplate redisTemplate;

    @Override
    public Set<Long> getAnsweredIds(Long accountId, LocalDate date) {
        Set<String> members = redisTemplate.opsForSet().members(answeredKey(accountId, date));
        if (members == null) {
            return Set.of();
        }
        return members.stream()
                .map(Long::parseLong)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void recordAnswer(Long accountId, Long questionId, LocalDate date) {
        String key = answeredKey(accountId, date);
        redisTemplate.opsForSet().add(key, String.valueOf(questionId));
        redisTemplate.expire(key, ttlUntilTomorrow(date));
    }

    private String answeredKey(Long accountId, LocalDate date) {
        return RedisKey.DAILY_RECOMMENDATION_ANSWERED.format(accountId, date.format(DATE_FORMAT));
    }

    private Duration ttlUntilTomorrow(LocalDate date) {
        return Duration.between(LocalDateTime.now(), date.plusDays(1).atStartOfDay());
    }
}
