package com.ktb.ai.feedback.worker;

import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;
import com.ktb.ai.feedback.service.AiFeedbackService;
import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerFeedbackInbox;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.repository.AnswerFeedbackInboxRepository;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.common.dto.ApiResponse;
import com.ktb.metric.domain.AnswerMetric;
import com.ktb.metric.domain.Metric;
import com.ktb.metric.repository.AnswerMetricRepository;
import com.ktb.metric.repository.MetricRepository;
import com.ktb.notification.relay.OutboxMessage;
import com.ktb.question.domain.Question;
import com.ktb.question.repository.QuestionRepository;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * RabbitMQ 기반 비동기 AI 피드백 처리 워커.
 * Outbox 릴레이로부터 answer.feedback-processing 큐 메시지를 수신하여 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "answer.feedback.rabbitmq.enabled", havingValue = "true")
public class AiFeedbackWorker {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AiFeedbackService aiFeedbackService;
    private final MetricRepository metricRepository;
    private final AnswerMetricRepository answerMetricRepository;
    private final AnswerFeedbackInboxRepository answerFeedbackInboxRepository;
    private final TransactionTemplate transactionTemplate;

    @RabbitListener(
        queues = "${answer.feedback.rabbitmq.queue}",
        containerFactory = "manualAckListenerContainerFactory"
    )
    public void onAnswerFeedbackRequested(
            @Payload OutboxMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        if (answerFeedbackInboxRepository.existsByEventId(message.messageId())) {
            log.debug("Duplicate AI feedback event skipped - messageId={}", message.messageId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        try {
            Map<String, Object> payload = message.payload();
            Long answerId = toLong(payload.get("answerId"));
            Long accountId = toLong(payload.get("accountId"));
            Long questionId = toLong(payload.get("questionId"));

            // TX1: 상태를 AI_FEEDBACK_PROCESSING으로 전이
            transitionToProcessing(answerId);

            // AI 호출 (트랜잭션 외부)
            Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found: " + answerId));
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

            ApiResponse<AiFeedbackResponse> aiResponse = aiFeedbackService.evaluateSync(
                accountId,
                question.getId(),
                question.getType(),
                question.getCategory(),
                answer.getType(),
                question.getContent(),
                answer.getContent()
            );

            // TX2: 결과 저장 + 상태 전이 + 인박스 기록
            persistAndComplete(message.messageId(), answer, question, aiResponse);

            channel.basicAck(deliveryTag, false);
            log.info("AI feedback processed - messageId={}, answerId={}", message.messageId(), answerId);

        } catch (DataIntegrityViolationException e) {
            log.debug("Inbox race condition resolved - messageId={}", message.messageId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to process AI feedback - messageId={}, error={}",
                message.messageId(), e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private void transitionToProcessing(Long answerId) {
        transactionTemplate.executeWithoutResult(status -> {
            Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found: " + answerId));
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
            answerRepository.save(answer);
        });
    }

    private void persistAndComplete(
            String eventId,
            Answer answer,
            Question question,
            ApiResponse<AiFeedbackResponse> aiResponse) {

        AiFeedbackResponse data = aiResponse.data();

        transactionTemplate.executeWithoutResult(status -> {
            saveMetrics(answer, data);

            String feedback = buildFeedbackText(data);
            answer.setAiFeedback(feedback);
            answer.transitionTo(AnswerStatus.COMPLETED);
            answerRepository.save(answer);

            answerFeedbackInboxRepository.save(
                new AnswerFeedbackInbox(eventId, answer.getId())
            );
        });

        log.debug("AI feedback persisted - answerId={}", answer.getId());
    }

    private void saveMetrics(Answer answer, AiFeedbackResponse data) {
        if (data.metrics() == null || data.metrics().isEmpty()) {
            return;
        }

        List<String> metricNames = data.metrics().stream()
            .map(m -> m.name())
            .toList();

        Map<String, Metric> metricMap = metricRepository.findAllByNameIn(metricNames)
            .stream()
            .collect(Collectors.toMap(Metric::getName, m -> m));

        List<AnswerMetric> answerMetrics = new ArrayList<>();
        for (var aiMetric : data.metrics()) {
            Metric metric = metricMap.computeIfAbsent(
                aiMetric.name(),
                name -> metricRepository.save(Metric.create(name, ""))
            );
            int score = aiMetric.score() == null ? 1 : aiMetric.score();
            answerMetrics.add(AnswerMetric.create(answer, metric, score));
        }

        answerMetricRepository.saveAll(answerMetrics);
    }

    private String buildFeedbackText(AiFeedbackResponse data) {
        if (data.feedback() == null) {
            return null;
        }
        String strengths = data.feedback().strengths();
        String improvements = data.feedback().improvements();
        if (strengths == null && improvements == null) {
            return null;
        }
        return (strengths != null ? strengths : "") + "\n\n" + (improvements != null ? improvements : "");
    }

    private Long toLong(Object value) {
        if (value instanceof Number num) {
            return num.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
