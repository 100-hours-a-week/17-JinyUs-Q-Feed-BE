package com.ktb.answer.service;

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
import com.ktb.answer.dto.KeywordCheckResult;
import com.ktb.answer.dto.response.list.AnswerListResponse;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.impl.AnswerApplicationServiceImpl;
import com.ktb.auth.domain.UserAccount;
import com.ktb.hashtag.domain.Hashtag;
import com.ktb.hashtag.exception.HashtagNotFoundException;
import com.ktb.hashtag.repository.AnswerHashtagRepository;
import com.ktb.hashtag.repository.HashtagRepository;
import com.ktb.metric.domain.AnswerMetric;
import com.ktb.metric.repository.AnswerMetricRepository;
import com.ktb.notification.repository.NotificationOutboxRepository;
import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AnswerApplicationService 단위 테스트")
class AnswerApplicationServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerCommandService answerCommandService;

    @Mock
    private AnswerQueryService answerQueryService;

    @Mock
    private ImmediateFeedbackService immediateFeedbackService;

    @Mock
    private AnswerHashtagRepository answerHashtagRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private AnswerMetricRepository answerMetricRepository;

    @Mock
    private AbuseGuard abuseGuard;

    @Mock
    private NotificationOutboxRepository notificationOutboxRepository;

    private AnswerApplicationServiceImpl answerApplicationService;

    private static final Long ACCOUNT_ID = 1L;
    private static final Long QUESTION_ID = 100L;
    private static final Long ANSWER_ID = 1000L;
    private static final String CLIENT_IP = "127.0.0.1";
    private static final String VALID_ANSWER_TEXT = "유효한 답변 텍스트입니다.";

    @BeforeEach
    void setUp() {
        AnswerFeedbackProperties props = new AnswerFeedbackProperties();
        answerApplicationService = new AnswerApplicationServiceImpl(
                answerRepository,
                answerCommandService,
                answerQueryService,
                immediateFeedbackService,
                answerHashtagRepository,
                hashtagRepository,
                answerMetricRepository,
                abuseGuard,
                props,
                notificationOutboxRepository
        );
    }

    @Nested
    @DisplayName("submit() 테스트")
    class SubmitTest {

        @Test
        @DisplayName("정상 제출 시 PROCESSING 상태로 결과 반환")
        void submit_WithValidData_ShouldReturnProcessingResult() {
            // Given
            AnswerSubmitCommand command = new AnswerSubmitCommand(
                    QUESTION_ID,
                    VALID_ANSWER_TEXT,
                    AnswerType.PRACTICE_INTERVIEW
            );

            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.SUBMITTED);
            AbuseGuardResult abuseResult = AbuseGuardResult.accept();
            ImmediateFeedbackResult immediateFeedback = new ImmediateFeedbackResult(
                    List.of(new KeywordCheckResult(1L, "키워드1", true))
            );
            Hashtag mockHashtag = mock(Hashtag.class);

            when(abuseGuard.check(any(AbuseCheckContext.class))).thenReturn(abuseResult);
            when(answerCommandService.create(anyLong(), anyLong(), anyString(), any()))
                    .thenReturn(mockAnswer);
            when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);
            when(immediateFeedbackService.evaluate(eq(QUESTION_ID), anyString()))
                    .thenReturn(immediateFeedback);
            when(hashtagRepository.findById(anyLong())).thenReturn(Optional.of(mockHashtag));

            // When
            AnswerSubmitResult result = answerApplicationService.submit(ACCOUNT_ID, command, CLIENT_IP);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.answerId()).isEqualTo(ANSWER_ID);
            assertThat(result.aiFeedbackStatus()).isEqualTo(FeedbackStatus.PROCESSING);
            verify(answerCommandService).validateContent(VALID_ANSWER_TEXT);
            verify(answerHashtagRepository).saveAll(any());
        }

        @Test
        @DisplayName("abuse 체크 실패 시 NOT_AVAILABLE 상태 반환")
        void submit_WithAbuseNoFeedback_ShouldReturnNotAvailable() {
            // Given
            AnswerSubmitCommand command = new AnswerSubmitCommand(
                    QUESTION_ID,
                    VALID_ANSWER_TEXT,
                    AnswerType.PRACTICE_INTERVIEW
            );

            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.SUBMITTED);
            AbuseGuardResult abuseResult = AbuseGuardResult.acceptNoFeedback(
                    "ContentQualityGuard",
                    "Low quality content",
                    30
            );
            ImmediateFeedbackResult immediateFeedback = new ImmediateFeedbackResult(Collections.emptyList());

            when(abuseGuard.check(any(AbuseCheckContext.class))).thenReturn(abuseResult);
            when(answerCommandService.create(anyLong(), anyLong(), anyString(), any()))
                    .thenReturn(mockAnswer);
            when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);
            when(immediateFeedbackService.evaluate(eq(QUESTION_ID), anyString()))
                    .thenReturn(immediateFeedback);

            // When
            AnswerSubmitResult result = answerApplicationService.submit(ACCOUNT_ID, command, CLIENT_IP);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.answerId()).isEqualTo(ANSWER_ID);
            assertThat(result.aiFeedbackStatus()).isEqualTo(FeedbackStatus.NOT_AVAILABLE);
            verify(answerCommandService).transitionStatus(mockAnswer, AnswerStatus.NOT_AVAILABLE);
        }

        @Test
        @DisplayName("해시태그 미존재 시 HashtagNotFoundException 발생")
        void submit_WithNonExistentHashtag_ShouldThrowException() {
            // Given
            AnswerSubmitCommand command = new AnswerSubmitCommand(
                    QUESTION_ID,
                    VALID_ANSWER_TEXT,
                    AnswerType.PRACTICE_INTERVIEW
            );

            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.SUBMITTED);
            AbuseGuardResult abuseResult = AbuseGuardResult.accept();
            ImmediateFeedbackResult immediateFeedback = new ImmediateFeedbackResult(
                    List.of(new KeywordCheckResult(999L, "존재하지않는키워드", true))
            );

            when(abuseGuard.check(any(AbuseCheckContext.class))).thenReturn(abuseResult);
            when(answerCommandService.create(anyLong(), anyLong(), anyString(), any()))
                    .thenReturn(mockAnswer);
            when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);
            when(immediateFeedbackService.evaluate(eq(QUESTION_ID), anyString()))
                    .thenReturn(immediateFeedback);
            when(hashtagRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() ->
                    answerApplicationService.submit(ACCOUNT_ID, command, CLIENT_IP))
                    .isInstanceOf(HashtagNotFoundException.class);
        }

        @Test
        @DisplayName("빈 키워드 목록일 때 해시태그 저장 호출")
        void submit_WithEmptyKeywords_ShouldSaveEmptyHashtags() {
            // Given
            AnswerSubmitCommand command = new AnswerSubmitCommand(
                    QUESTION_ID,
                    VALID_ANSWER_TEXT,
                    AnswerType.PRACTICE_INTERVIEW
            );

            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.SUBMITTED);
            AbuseGuardResult abuseResult = AbuseGuardResult.accept();
            ImmediateFeedbackResult immediateFeedback = new ImmediateFeedbackResult(Collections.emptyList());

            when(abuseGuard.check(any(AbuseCheckContext.class))).thenReturn(abuseResult);
            when(answerCommandService.create(anyLong(), anyLong(), anyString(), any()))
                    .thenReturn(mockAnswer);
            when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);
            when(immediateFeedbackService.evaluate(eq(QUESTION_ID), anyString()))
                    .thenReturn(immediateFeedback);

            // When
            AnswerSubmitResult result = answerApplicationService.submit(ACCOUNT_ID, command, CLIENT_IP);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.aiFeedbackStatus()).isEqualTo(FeedbackStatus.PROCESSING);
            verify(answerHashtagRepository).saveAll(Collections.emptyList());
        }
    }

    @Nested
    @DisplayName("getList() 테스트")
    class GetListTest {

        @Test
        @DisplayName("목록 조회는 AnswerQueryService에 위임")
        void getList_ShouldDelegateToQueryService() {
            // Given
            AnswerListResponse expected = mock(AnswerListResponse.class);
            LocalDate dateFrom = LocalDate.now().minusDays(7);
            LocalDate dateTo = LocalDate.now();

            when(answerQueryService.getList(
                    eq(ACCOUNT_ID), isNull(), isNull(), isNull(),
                    eq(dateFrom), eq(dateTo), isNull(), eq(10)))
                    .thenReturn(expected);

            // When
            AnswerListResponse result = answerApplicationService.getList(
                    ACCOUNT_ID, null, null, null, dateFrom, dateTo, null, 10
            );

            // Then
            assertThat(result).isSameAs(expected);
            verify(answerQueryService).getList(
                    eq(ACCOUNT_ID), isNull(), isNull(), isNull(),
                    eq(dateFrom), eq(dateTo), isNull(), eq(10));
        }
    }

    @Nested
    @DisplayName("getDetail() 테스트")
    class GetDetailTest {

        @Test
        @DisplayName("상세 조회는 AnswerQueryService에 위임")
        void getDetail_ShouldDelegateToQueryService() {
            // Given
            AnswerDetailResult expected = mock(AnswerDetailResult.class);
            when(answerQueryService.getDetail(ACCOUNT_ID, ANSWER_ID)).thenReturn(expected);

            // When
            AnswerDetailResult result = answerApplicationService.getDetail(
                    ACCOUNT_ID, ANSWER_ID, AnswerDetailQuery.empty()
            );

            // Then
            assertThat(result).isSameAs(expected);
            verify(answerQueryService).getDetail(ACCOUNT_ID, ANSWER_ID);
        }
    }

    @Nested
    @DisplayName("getFeedback() 테스트")
    class GetFeedbackTest {

        @Test
        @DisplayName("존재하지 않는 답변의 피드백 조회 시 예외 발생")
        void getFeedback_WithNonExistentAnswer_ShouldThrowException() {
            // Given
            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() ->
                    answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID))
                    .isInstanceOf(AnswerNotFoundException.class);
        }

        @Test
        @DisplayName("PROCESSING 상태일 때 estimatedTimeSeconds 포함")
        void getFeedback_WithProcessingStatus_ShouldIncludeEstimatedTime() {
            // Given
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.AI_FEEDBACK_PROCESSING);

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));

            // When
            FeedbackResult result = answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID);

            // Then
            assertThat(result.status()).isEqualTo(FeedbackStatus.PROCESSING);
            assertThat(result.estimatedTimeSeconds()).isEqualTo(30);
            assertThat(result.metrics()).isNull();
            assertThat(result.comment()).isNull();
        }

        @Test
        @DisplayName("NOT_AVAILABLE 상태일 때 metrics, comment가 null")
        void getFeedback_WithNotAvailableStatus_ShouldReturnEmptyFeedback() {
            // Given
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.NOT_AVAILABLE);

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));

            // When
            FeedbackResult result = answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID);

            // Then
            assertThat(result.status()).isEqualTo(FeedbackStatus.NOT_AVAILABLE);
            assertThat(result.estimatedTimeSeconds()).isNull();
            assertThat(result.metrics()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태일 때 적절한 응답 반환")
        void getFeedback_WithFailedStatus_ShouldReturnFailedResponse() {
            // Given
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.FAILED);

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));

            // When
            FeedbackResult result = answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID);

            // Then
            assertThat(result.status()).isEqualTo(FeedbackStatus.FAILED);
            assertThat(result.estimatedTimeSeconds()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태일 때 metrics와 comment 포함")
        void getFeedback_WithCompletedStatus_ShouldIncludeMetrics() {
            // Given
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.COMPLETED);
            String aiFeedback = "잘 답변하셨습니다.";
            when(mockAnswer.getAiFeedback()).thenReturn(aiFeedback);
            when(mockAnswer.getUpdatedAt()).thenReturn(LocalDateTime.now());

            AnswerMetric metric = mock(AnswerMetric.class);
            when(metric.getMetricName()).thenReturn("논리성");
            when(metric.getScore()).thenReturn(85);

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));
            when(answerMetricRepository.findByAnswerIdWithMetric(ANSWER_ID))
                    .thenReturn(List.of(metric));

            // When
            FeedbackResult result = answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID);

            // Then
            assertThat(result.status()).isEqualTo(FeedbackStatus.COMPLETED);
            assertThat(result.metrics()).containsEntry("논리성", 85);
            assertThat(result.comment()).isEqualTo(aiFeedback);
            assertThat(result.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("빈 metrics 리스트일 때 빈 Map 반환")
        void getFeedback_WithEmptyMetrics_ShouldReturnEmptyMap() {
            // Given
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.COMPLETED);
            when(mockAnswer.getAiFeedback()).thenReturn("피드백 내용");
            when(mockAnswer.getUpdatedAt()).thenReturn(LocalDateTime.now());

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));
            when(answerMetricRepository.findByAnswerIdWithMetric(ANSWER_ID))
                    .thenReturn(Collections.emptyList());

            // When
            FeedbackResult result = answerApplicationService.getFeedback(ACCOUNT_ID, ANSWER_ID);

            // Then
            assertThat(result.status()).isEqualTo(FeedbackStatus.COMPLETED);
            assertThat(result.metrics()).isEmpty();
        }

        @Test
        @DisplayName("다른 사용자의 피드백 조회 시 예외 발생")
        void getFeedback_WithOtherUserAnswer_ShouldThrowException() {
            // Given
            Long otherAccountId = 999L;
            Answer mockAnswer = createMockAnswer(ANSWER_ID, ACCOUNT_ID, AnswerStatus.COMPLETED);

            when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(mockAnswer));
            doThrow(new AnswerAccessDeniedException(ANSWER_ID, otherAccountId))
                    .when(answerCommandService).validateOwnership(mockAnswer, otherAccountId);

            // When & Then
            assertThatThrownBy(() ->
                    answerApplicationService.getFeedback(otherAccountId, ANSWER_ID))
                    .isInstanceOf(AnswerAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("submitWithSession() 테스트")
    class SubmitWithSessionTest {

        @Test
        @DisplayName("세션 기반 제출은 아직 미구현으로 UnsupportedOperationException 발생")
        void submitWithSession_ShouldThrowUnsupportedOperationException() {
            // Given
            String sessionId = "session-123";
            AnswerSubmitCommand command = new AnswerSubmitCommand(
                    QUESTION_ID,
                    VALID_ANSWER_TEXT,
                    AnswerType.REAL_INTERVIEW
            );

            // When & Then
            assertThatThrownBy(() ->
                    answerApplicationService.submitWithSession(ACCOUNT_ID, sessionId, command))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("not yet implemented");
        }
    }

    // Helper methods

    private Answer createMockAnswer(Long answerId, Long accountId, AnswerStatus status) {
        Answer answer = mock(Answer.class);
        UserAccount account = mock(UserAccount.class);

        when(answer.getId()).thenReturn(answerId);
        when(answer.getAccount()).thenReturn(account);
        when(account.getId()).thenReturn(accountId);
        when(answer.getStatus()).thenReturn(status);
        when(answer.getContent()).thenReturn(VALID_ANSWER_TEXT);
        when(answer.getType()).thenReturn(AnswerType.PRACTICE_INTERVIEW);
        when(answer.isOwnedBy(accountId)).thenReturn(true);

        return answer;
    }

    private Answer createMockAnswerWithQuestion(Long answerId, Long accountId) {
        Answer answer = mock(Answer.class);
        UserAccount account = mock(UserAccount.class);
        Question question = mock(Question.class);

        when(answer.getId()).thenReturn(answerId);
        when(answer.getAccount()).thenReturn(account);
        when(account.getId()).thenReturn(accountId);
        when(answer.getStatus()).thenReturn(AnswerStatus.SUBMITTED);
        when(answer.getContent()).thenReturn(VALID_ANSWER_TEXT);
        when(answer.getType()).thenReturn(AnswerType.PRACTICE_INTERVIEW);
        when(answer.getQuestion()).thenReturn(question);
        when(answer.isOwnedBy(accountId)).thenReturn(true);
        when(answer.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(question.getId()).thenReturn(QUESTION_ID);
        when(question.getContent()).thenReturn("테스트 질문입니다.");
        when(question.getCategory()).thenReturn(QuestionCategory.DB);
        when(question.getType()).thenReturn(QuestionType.CS);

        return answer;
    }
}
