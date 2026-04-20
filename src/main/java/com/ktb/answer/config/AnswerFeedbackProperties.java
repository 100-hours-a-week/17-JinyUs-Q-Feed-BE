package com.ktb.answer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "answer.feedback.rabbitmq")
public class AnswerFeedbackProperties {

    private boolean enabled = false;
    private String exchange = "answer.direct";
    private String dlx = "answer.dlx";
    private String queue = "answer.feedback-processing";
    private String dlq = "answer.feedback-processing.dlq";
    private String routingKey = "answer.feedback-processing";
    private String dlqRoutingKey = "dlq.answer.feedback-processing";
}
