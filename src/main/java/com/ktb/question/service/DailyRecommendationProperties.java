package com.ktb.question.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "question.recommendation")
public class DailyRecommendationProperties {
    private int candidateCount = 10;
}
