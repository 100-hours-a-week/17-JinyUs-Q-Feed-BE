package com.ktb.answer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "answer.feedback.rabbitmq.enabled", havingValue = "true")
@EnableConfigurationProperties(AnswerFeedbackProperties.class)
public class AnswerFeedbackRabbitConfig {

    private final AnswerFeedbackProperties props;

    public AnswerFeedbackRabbitConfig(AnswerFeedbackProperties props) {
        this.props = props;
    }

    @Bean
    public DirectExchange answerFeedbackDirectExchange() {
        return new DirectExchange(props.getExchange(), true, false);
    }

    @Bean
    public DirectExchange answerFeedbackDlx() {
        return new DirectExchange(props.getDlx(), true, false);
    }

    @Bean
    public Queue answerFeedbackProcessingQueue() {
        return QueueBuilder.durable(props.getQueue())
            .withArgument("x-dead-letter-exchange", props.getDlx())
            .withArgument("x-dead-letter-routing-key", props.getDlqRoutingKey())
            .withArgument("x-message-ttl", 86_400_000L)
            .build();
    }

    @Bean
    public Queue answerFeedbackDlqQueue() {
        return QueueBuilder.durable(props.getDlq()).build();
    }

    @Bean
    public Binding answerFeedbackProcessingBinding() {
        return BindingBuilder
            .bind(answerFeedbackProcessingQueue())
            .to(answerFeedbackDirectExchange())
            .with(props.getRoutingKey());
    }

    @Bean
    public Binding answerFeedbackDlqBinding() {
        return BindingBuilder
            .bind(answerFeedbackDlqQueue())
            .to(answerFeedbackDlx())
            .with(props.getDlqRoutingKey());
    }
}
