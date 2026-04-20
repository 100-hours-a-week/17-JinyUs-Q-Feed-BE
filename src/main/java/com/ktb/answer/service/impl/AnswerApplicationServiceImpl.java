package com.ktb.answer.service.impl;

import com.ktb.abuse.core.AbuseCheckContext;
import com.ktb.abuse.core.AbuseGuard;
import com.ktb.abuse.core.AbuseGuardResult;
import com.ktb.answer.config.AnswerFeedbackProperties;
import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.dto.AnswerDetailQuery;
import com.ktb.answer.dto.AnswerDetailResult;
import com.ktb.answer.dto.AnswerSubmitCommand;
import com.ktb.answer.dto.AnswerSubmitResult;
import com.ktb.answer.dto.FeedbackResult;
import com.ktb.answer.dto.FeedbackStatus;
import com.ktb.answer.dto.ImmediateFeedbackResult;
import com.ktb.answer.dto.response.list.AnswerListResponse;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AnswerApplicationService;
import com.ktb.answer.service.AnswerCommandService;
import com.ktb.answer.service.AnswerQueryService;
import com.ktb.answer.service.ImmediateFeedbackService;
import com.ktb.file.exception.FileAlreadyDeletedException;
import com.ktb.file.exception.FileExtensionNotAllowedException;
import com.ktb.file.exception.FileNotFoundException;
import com.ktb.file.exception.FileSizeExceededException;
import com.ktb.file.exception.FileStorageMigrationException;
import com.ktb.hashtag.domain.AnswerHashtag;
import com.ktb.hashtag.domain.Hashtag;
import com.ktb.hashtag.exception.HashtagNotFoundException;
import com.ktb.hashtag.repository.AnswerHashtagRepository;
import com.ktb.hashtag.repository.HashtagRepository;
import com.ktb.metric.domain.AnswerMetric;
import com.ktb.metric.repository.AnswerMetricRepository;
import com.ktb.notification.domain.NotificationOutbox;
import com.ktb.notification.repository.NotificationOutboxRepository;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import com.ktb.question.exception.QuestionDisabledException;
import com.ktb.question.exception.QuestionNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(AnswerFeedbackProperties.class)
public class AnswerApplicationServiceImpl implements AnswerApplicationService {

    private final AnswerRepository answerRepository;
    private final AnswerCommandService answerCommandService;
    private final AnswerQueryService answerQueryService;
    private final ImmediateFeedbackService immediateFeedbackService;
    private final AnswerHashtagRepository answerHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final AnswerMetricRepository answerMetricRepository;
    private final AbuseGuard abuseGuard;
    private final AnswerFeedbackProperties answerFeedbackProperties;
    private final NotificationOutboxRepository notificationOutboxRepository;

    @Override
    public AnswerListResponse getList(
            Long accountId,
            AnswerType type,
            QuestionCategory category,
            QuestionType questionType,
            LocalDate dateFrom,
            LocalDate dateTo,
            String cursor,
            Integer limit
    ) {
        return answerQueryService.getList(accountId, type, category, questionType, dateFrom, dateTo, cursor, limit);
    }

    @Override
    @Transactional
    public AnswerSubmitResult submit(Long accountId, AnswerSubmitCommand command, String clientIp)
            throws QuestionNotFoundException, QuestionDisabledException,
            FileSizeExceededException, FileExtensionNotAllowedException,
            FileNotFoundException, FileAlreadyDeletedException, FileStorageMigrationException {

        log.info("Submitting answer - questionId={}, accountId={}", command.questionId(), accountId);

        answerCommandService.validateContent(command.answerText());

        AbuseCheckContext abuseContext = AbuseCheckContext.of(
                accountId,
                command.questionId(),
                clientIp,
                command.answerText()
        );
        AbuseGuardResult abuseResult = abuseGuard.check(abuseContext);

        Answer answer = answerRepository.save(
                answerCommandService.create(accountId, command.questionId(), command.answerText(), command.answerType())
        );

        ImmediateFeedbackResult immediateFeedback = immediateFeedbackService.evaluate(
                command.questionId(),
                answer.getContent()
        );

        saveAnswerHashtags(answer, immediateFeedback);

        if (!abuseResult.shouldProvideFeedback()) {
            answerCommandService.transitionStatus(answer, AnswerStatus.NOT_AVAILABLE);
            log.info("Answer saved without AI feedback - accountId={}, reason={}",
                    accountId, abuseResult.getReason());
            return AnswerSubmitResult.noAiFeedback(answer.getId(), immediateFeedback);
        }

        if (answerFeedbackProperties.isEnabled()) {
            saveAiFeedbackOutbox(answer, accountId, command.questionId());
            log.info("AI feedback outbox saved - answerId={}", answer.getId());
        } else {
            log.debug("AI feedback outbox disabled - answerId={}", answer.getId());
        }

        return AnswerSubmitResult.processing(answer.getId(), immediateFeedback);
    }

    private void saveAiFeedbackOutbox(Answer answer, Long accountId, Long questionId) {
        String eventId = UUID.randomUUID().toString();
        Map<String, Object> payload = Map.of(
            "eventId", eventId,
            "answerId", answer.getId(),
            "accountId", accountId,
            "questionId", questionId
        );
        notificationOutboxRepository.save(NotificationOutbox.of(
            "ANSWER_SUBMITTED",
            "ANSWER_FEEDBACK",
            answer.getId().toString(),
            eventId,
            answerFeedbackProperties.getExchange(),
            answerFeedbackProperties.getRoutingKey(),
            payload
        ));
    }

    private void saveAnswerHashtags(Answer answer, ImmediateFeedbackResult feedback) {
        List<AnswerHashtag> answerHashtags = feedback.keywords().stream()
            .map(keywordResult -> {
                Hashtag hashtag = hashtagRepository.findById(keywordResult.keywordId())
                    .orElseThrow(() -> new HashtagNotFoundException(keywordResult.keywordId()));
                return AnswerHashtag.create(answer, hashtag, keywordResult.included());
            })
            .toList();

        answerHashtagRepository.saveAll(answerHashtags);

        log.debug("AnswerHashtags saved - answerId={}, total={}, included={}",
                  answer.getId(),
                  answerHashtags.size(),
                  answerHashtags.stream().filter(ah -> ah.isIncluded()).count());
    }

    @Override
    @Transactional
    public AnswerSubmitResult submitWithSession(Long accountId, String sessionId, AnswerSubmitCommand command)
            throws QuestionNotFoundException {

        log.info("Submitting session answer - sessionId={}, questionId={}, accountId={}",
                sessionId, command.questionId(), accountId);

        // TODO: ANSWER_SESSION 엔티티 구현 후 활성화
        throw new UnsupportedOperationException("Session-based answer submission not yet implemented");
    }

    @Override
    public AnswerDetailResult getDetail(Long accountId, Long answerId, AnswerDetailQuery query)
        throws AnswerNotFoundException, AnswerAccessDeniedException {
        return answerQueryService.getDetail(accountId, answerId);
    }

    @Override
    public FeedbackResult getFeedback(Long accountId, Long answerId)
            throws AnswerNotFoundException, AnswerAccessDeniedException {

        log.debug("Retrieving feedback - answerId={}, accountId={}", answerId, accountId);

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));
        answerCommandService.validateOwnership(answer, accountId);

        return buildFeedbackResult(answer);
    }

    private FeedbackResult buildFeedbackResult(Answer answer) {
        FeedbackStatus status = FeedbackStatus.from(answer.getStatus());

        if (status == FeedbackStatus.PROCESSING) {
            return new FeedbackResult(status, 30, null, null, null);
        }

        if (status != FeedbackStatus.COMPLETED) {
            return new FeedbackResult(status, null, null, null, null);
        }

        List<AnswerMetric> metrics = answerMetricRepository.findByAnswerIdWithMetric(answer.getId());
        Map<String, Integer> metricsMap = metrics.stream()
                .collect(Collectors.toMap(
                        AnswerMetric::getMetricName,
                        AnswerMetric::getScore
                ));

        return new FeedbackResult(
                status,
                null,
                metricsMap,
                answer.getAiFeedback(),
                answer.getUpdatedAt() != null
                        ? answer.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : null
        );
    }
}
