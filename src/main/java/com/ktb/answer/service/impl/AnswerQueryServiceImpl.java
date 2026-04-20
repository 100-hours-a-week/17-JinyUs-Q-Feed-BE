package com.ktb.answer.service.impl;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.domain.policy.QuestionTypeFilterPolicy;
import com.ktb.answer.dto.AiFeedbackSummary;
import com.ktb.answer.dto.AnswerContentResult;
import com.ktb.answer.dto.AnswerDetailResult;
import com.ktb.answer.dto.AnswerListCursor;
import com.ktb.answer.dto.FeedbackStatus;
import com.ktb.answer.dto.ImmediateFeedbackResult;
import com.ktb.answer.dto.KeywordCheckResult;
import com.ktb.answer.dto.QuestionSummary;
import com.ktb.answer.dto.response.detail.AnswerQuestionInfo;
import com.ktb.answer.dto.response.list.AnswerListResponse;
import com.ktb.answer.dto.response.list.AnswerSummary;
import com.ktb.answer.dto.response.list.FeedbackInfo;
import com.ktb.answer.dto.response.list.PaginationInfo;
import com.ktb.answer.exception.AnswerListInvalidInputException;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AnswerQueryService;
import com.ktb.answer.service.CursorCodec;
import com.ktb.hashtag.domain.AnswerHashtag;
import com.ktb.hashtag.repository.AnswerHashtagRepository;
import com.ktb.interview.service.impl.flow.InterviewSessionFeedbackQueryFlowService;
import com.ktb.interview.dto.response.InterviewSessionFinalFeedbackResponse;
import com.ktb.interview.exception.InterviewSessionInvalidStateException;
import com.ktb.metric.domain.AnswerMetric;
import com.ktb.metric.repository.AnswerMetricRepository;
import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryServiceImpl implements AnswerQueryService {

    private final AnswerRepository answerRepository;
    private final AnswerHashtagRepository answerHashtagRepository;
    private final AnswerMetricRepository answerMetricRepository;
    private final QuestionTypeFilterPolicy questionTypeFilterPolicy;
    private final CursorCodec cursorCodec;
    private final InterviewSessionFeedbackQueryFlowService interviewSessionFeedbackQueryFlowService;

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
        log.info("getAnswerList - accountId={}, type={}, category={}, questionType={}, dateFrom={}, dateTo={}, cursorProvided={}, limit={}",
                accountId, type, category, questionType, dateFrom, dateTo,
                cursor != null && !cursor.isBlank(), limit);

        questionTypeFilterPolicy.validateOrThrow(questionType);
        LocalDateRange dateRange = resolveDateRange(dateFrom, dateTo);
        int resolvedLimit = resolveLimit(limit);
        AnswerListCursor cursorPayload = cursorCodec.decode(cursor);
        PageRequest pageRequest = PageRequest.of(0, resolvedLimit);

        LocalDateTime from = dateRange.start().atStartOfDay();
        LocalDateTime to = dateRange.end().atTime(LocalTime.MAX);

        Slice<Answer> answers = cursorPayload == null
                ? answerRepository.findByAccountIdWithFiltersNoCursor(
                        accountId, type, category, questionType, from, to, pageRequest)
                : answerRepository.findByAccountIdWithFilters(
                        accountId, type, category, questionType, from, to,
                        cursorPayload.lastCreatedAt(), cursorPayload.lastAnswerId(), pageRequest);

        log.info("getAnswerList completed - accountId={}, fetchedSize={}, hasNext={}, resolvedLimit={}",
                accountId, answers.getContent().size(), answers.hasNext(), resolvedLimit);

        return toAnswerListResponse(answers, resolvedLimit);
    }

    @Override
    public AnswerDetailResult getDetail(Long accountId, Long answerId) {
        log.info("getAnswerDetail - accountId={}, answerId={}", accountId, answerId);

        Answer answer = answerRepository.findByIdWithQuestion(answerId);
        if (answer == null) {
            log.warn("getAnswerDetail not found - accountId={}, answerId={}", accountId, answerId);
            throw new AnswerNotFoundException(answerId);
        }

        if (!answer.isOwnedBy(accountId)) {
            log.warn("Answer access denied - answerId={}, requestedAccountId={}", answerId, accountId);
            throw new com.ktb.answer.exception.AnswerAccessDeniedException(answerId, accountId);
        }

        AnswerContentResult answerContent = new AnswerContentResult(
                answer.getContent(),
                null,
                null,
                answer.getCreatedAt() == null ? null : answer.getCreatedAt().toString()
        );

        boolean isRealInterview = answer.getType() == AnswerType.REAL_INTERVIEW;

        QuestionSummary questionSummary = null;
        if (!isRealInterview) {
            Question question = answer.getQuestion();
            questionSummary = new QuestionSummary(
                    question.getId(),
                    question.getContent(),
                    question.getCategory().name(),
                    question.getType().name()
            );
        }

        ImmediateFeedbackResult immediateFeedback = null;
        if (!isRealInterview) {
            immediateFeedback = loadImmediateFeedback(answerId);
        }

        AiFeedbackSummary aiFeedback = null;
        if (!isRealInterview) {
            aiFeedback = loadAiFeedback(answer);
        }

        InterviewSessionFinalFeedbackResponse sessionFinalFeedback = null;
        if (isRealInterview) {
            sessionFinalFeedback = loadRealInterviewSessionFinalFeedback(accountId, answer);
            log.debug("getAnswerDetail real mode session feedback resolved - accountId={}, answerId={}, hasFeedback={}",
                    accountId, answerId, sessionFinalFeedback != null);
        }

        AnswerDetailResult result = new AnswerDetailResult(
                answer.getId(),
                answer.getStatus(),
                answer.getType(),
                questionSummary,
                answerContent,
                immediateFeedback,
                aiFeedback,
                sessionFinalFeedback
        );

        log.info("getAnswerDetail completed - accountId={}, answerId={}, status={}",
                accountId, answerId, answer.getStatus());
        return result;
    }

    private InterviewSessionFinalFeedbackResponse loadRealInterviewSessionFinalFeedback(Long accountId, Answer answer) {
        if (answer.getStatus() != AnswerStatus.COMPLETED) {
            return null;
        }

        String sessionId = answer.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            log.warn("getAnswerDetail real mode sessionId missing - accountId={}, answerId={}",
                    accountId, answer.getId());
            return null;
        }

        try {
            InterviewSessionFinalFeedbackResponse sessionFeedback =
                    interviewSessionFeedbackQueryFlowService.getSessionFeedbackCompleted(accountId, sessionId);
            return new InterviewSessionFinalFeedbackResponse(
                    answer.getId(),
                    sessionFeedback.userId(),
                    sessionFeedback.questionId(),
                    sessionFeedback.sessionId(),
                    sessionFeedback.status(),
                    sessionFeedback.badCaseFeedback(),
                    sessionFeedback.metrics(),
                    sessionFeedback.keywordResult(),
                    sessionFeedback.topicsFeedback(),
                    sessionFeedback.overallFeedback(),
                    sessionFeedback.interviewHistory()
            );
        } catch (InterviewSessionInvalidStateException e) {
            log.warn("getAnswerDetail real mode final feedback unavailable - accountId={}, answerId={}, sessionId={}, reason={}",
                    accountId, answer.getId(), sessionId, e.getMessage());
            return null;
        }
    }

    private ImmediateFeedbackResult loadImmediateFeedback(Long answerId) {
        List<AnswerHashtag> answerHashtags = answerHashtagRepository.findByAnswerIdWithHashtag(answerId);
        List<KeywordCheckResult> keywords = answerHashtags.stream()
                .map(ah -> new KeywordCheckResult(
                        ah.getHashtag().getId(),
                        ah.getHashtag().getName(),
                        ah.isIncluded()
                ))
                .toList();
        return new ImmediateFeedbackResult(keywords);
    }

    private AiFeedbackSummary loadAiFeedback(Answer answer) {
        FeedbackStatus status = FeedbackStatus.from(answer.getStatus());
        if (status != FeedbackStatus.COMPLETED) {
            return new AiFeedbackSummary(status, null, null);
        }

        List<AnswerMetric> metrics = answerMetricRepository.findByAnswerIdWithMetric(answer.getId());
        Map<String, Integer> metricsMap = metrics.stream()
                .collect(Collectors.toMap(
                        AnswerMetric::getMetricName,
                        AnswerMetric::getScore
                ));
        return new AiFeedbackSummary(status, metricsMap, answer.getAiFeedback());
    }

    private LocalDateRange resolveDateRange(LocalDate dateFrom, LocalDate dateTo) {
        LocalDate resolvedTo = dateTo == null ? LocalDate.now() : dateTo;
        LocalDate resolvedFrom = dateFrom == null ? resolvedTo.minusMonths(1) : dateFrom;

        if (resolvedFrom.isAfter(resolvedTo)) {
            log.warn("Invalid date range - dateFrom={}, dateTo={}", resolvedFrom, resolvedTo);
            throw new AnswerListInvalidInputException("dateFrom must be before or equal to dateTo");
        }
        return new LocalDateRange(resolvedFrom, resolvedTo);
    }

    private int resolveLimit(Integer limit) {
        int resolved = limit == null ? 10 : limit;
        if (resolved < 1 || resolved > 50) {
            log.warn("Invalid list limit - limit={}", resolved);
            throw new AnswerListInvalidInputException("limit must be between 1 and 50");
        }
        return resolved;
    }

    private AnswerListResponse toAnswerListResponse(Slice<Answer> answers, int limit) {
        List<AnswerSummary> records = answers.getContent().stream()
                .map(answer -> new AnswerSummary(
                        answer.getId(),
                        answer.getType().name(),
                        answer.getCreatedAt() == null ? null : answer.getCreatedAt().toString(),
                        new AnswerQuestionInfo(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getContent(),
                                answer.getQuestion().getCategory().name()
                        ),
                        toFeedbackInfo(answer)
                ))
                .toList();

        String nextCursor = null;
        if (answers.hasNext() && !answers.getContent().isEmpty()) {
            Answer last = answers.getContent().getLast();
            nextCursor = cursorCodec.encode(new AnswerListCursor(last.getCreatedAt(), last.getId()));
        }

        PaginationInfo pagination = new PaginationInfo(limit, answers.hasNext(), nextCursor);
        return new AnswerListResponse(records, pagination);
    }

    private FeedbackInfo toFeedbackInfo(Answer answer) {
        AnswerStatus status = answer.getStatus();
        boolean available = status == AnswerStatus.COMPLETED;
        return new FeedbackInfo(available, status.name());
    }

    private record LocalDateRange(LocalDate start, LocalDate end) {
    }
}
