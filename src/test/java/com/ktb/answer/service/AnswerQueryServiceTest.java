package com.ktb.answer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.domain.policy.QuestionTypeFilterPolicy;
import com.ktb.answer.dto.AnswerDetailResult;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.impl.AnswerQueryServiceImpl;
import com.ktb.hashtag.repository.AnswerHashtagRepository;
import com.ktb.interview.dto.response.InterviewHistoryResponse;
import com.ktb.interview.dto.response.InterviewSessionFinalFeedbackResponse;
import com.ktb.interview.service.impl.flow.InterviewSessionFeedbackQueryFlowService;
import com.ktb.metric.repository.AnswerMetricRepository;
import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerQueryService 단위 테스트")
class AnswerQueryServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerHashtagRepository answerHashtagRepository;

    @Mock
    private AnswerMetricRepository answerMetricRepository;

    @Mock
    private QuestionTypeFilterPolicy questionTypeFilterPolicy;

    @Mock
    private CursorCodec cursorCodec;

    @Mock
    private InterviewSessionFeedbackQueryFlowService interviewSessionFeedbackQueryFlowService;

    @InjectMocks
    private AnswerQueryServiceImpl answerQueryService;

    private static final Long ACCOUNT_ID = 1L;

    @Nested
    @DisplayName("getDetail() 테스트")
    class GetDetailTest {

        @Test
        @DisplayName("실전모드 답변 조회 시 세션 최종 피드백을 포함한다")
        void getDetail_WithRealInterviewAnswer_ShouldIncludeSessionFinalFeedback() {
            // Given
            Long answerId = 101L;
            String sessionId = "session-real-101";
            Answer answer = mock(Answer.class);

            when(answerRepository.findByIdWithQuestion(answerId)).thenReturn(answer);
            when(answer.isOwnedBy(ACCOUNT_ID)).thenReturn(true);
            when(answer.getId()).thenReturn(answerId);
            when(answer.getType()).thenReturn(AnswerType.REAL_INTERVIEW);
            when(answer.getSessionId()).thenReturn(sessionId);
            when(answer.getStatus()).thenReturn(AnswerStatus.COMPLETED);
            when(answer.getContent()).thenReturn("실전 모드 답변");

            InterviewSessionFinalFeedbackResponse sessionFeedback = new InterviewSessionFinalFeedbackResponse(
                    null,
                    null,
                    null,
                    sessionId,
                    "COMPLETED",
                    null,
                    List.of(),
                    null,
                    List.of(),
                    null,
                    List.of(new InterviewHistoryResponse(
                            "질문",
                            "OS",
                            "답변",
                            "follow_up",
                            1,
                            1,
                            null,
                            null
                    ))
            );
            when(interviewSessionFeedbackQueryFlowService.getSessionFeedbackCompleted(ACCOUNT_ID, sessionId))
                    .thenReturn(sessionFeedback);

            // When
            AnswerDetailResult result = answerQueryService.getDetail(ACCOUNT_ID, answerId);

            // Then
            assertThat(result.sessionFinalFeedback()).isNotNull();
            assertThat(result.sessionFinalFeedback().answerId()).isEqualTo(answerId);
            assertThat(result.sessionFinalFeedback().sessionId()).isEqualTo(sessionId);
            assertThat(result.sessionFinalFeedback().interviewHistory()).hasSize(1);
            verify(interviewSessionFeedbackQueryFlowService).getSessionFeedbackCompleted(ACCOUNT_ID, sessionId);
        }

        @Test
        @DisplayName("연습모드 답변 조회 시 세션 최종 피드백 조회를 수행하지 않는다")
        void getDetail_WithPracticeInterviewAnswer_ShouldNotLoadSessionFinalFeedback() {
            // Given
            Long answerId = 202L;
            Answer answer = mock(Answer.class);
            Question question = mock(Question.class);

            when(answerRepository.findByIdWithQuestion(answerId)).thenReturn(answer);
            when(answer.isOwnedBy(ACCOUNT_ID)).thenReturn(true);
            when(answer.getId()).thenReturn(answerId);
            when(answer.getType()).thenReturn(AnswerType.PRACTICE_INTERVIEW);
            when(answer.getStatus()).thenReturn(AnswerStatus.COMPLETED);
            when(answer.getContent()).thenReturn("연습 모드 답변");
            when(answer.getQuestion()).thenReturn(question);
            when(question.getId()).thenReturn(10L);
            when(question.getContent()).thenReturn("질문");
            when(question.getCategory()).thenReturn(QuestionCategory.OS);
            when(question.getType()).thenReturn(QuestionType.CS);
            when(answerHashtagRepository.findByAnswerIdWithHashtag(answerId)).thenReturn(List.of());
            when(answerMetricRepository.findByAnswerIdWithMetric(answerId)).thenReturn(List.of());

            // When
            AnswerDetailResult result = answerQueryService.getDetail(ACCOUNT_ID, answerId);

            // Then
            assertThat(result.sessionFinalFeedback()).isNull();
            verify(interviewSessionFeedbackQueryFlowService, never()).getSessionFeedbackCompleted(anyLong(), anyString());
        }

        @Test
        @DisplayName("실전모드라도 답변 상태가 COMPLETED가 아니면 세션 최종 피드백을 조회하지 않는다")
        void getDetail_WithRealInterviewButIncompleteStatus_ShouldNotLoadSessionFinalFeedback() {
            // Given
            Long answerId = 303L;
            Answer answer = mock(Answer.class);

            when(answerRepository.findByIdWithQuestion(answerId)).thenReturn(answer);
            when(answer.isOwnedBy(ACCOUNT_ID)).thenReturn(true);
            when(answer.getId()).thenReturn(answerId);
            when(answer.getType()).thenReturn(AnswerType.REAL_INTERVIEW);
            when(answer.getStatus()).thenReturn(AnswerStatus.AI_FEEDBACK_PROCESSING);
            when(answer.getContent()).thenReturn("실전 모드 처리 중 답변");

            // When
            AnswerDetailResult result = answerQueryService.getDetail(ACCOUNT_ID, answerId);

            // Then
            assertThat(result.sessionFinalFeedback()).isNull();
            verify(interviewSessionFeedbackQueryFlowService, never()).getSessionFeedbackCompleted(anyLong(), anyString());
        }

        @Test
        @DisplayName("실전모드는 question/immediateFeedback/aiFeedback을 포함하지 않는다")
        void getDetail_WithRealInterview_ShouldIgnorePracticeOnlyFields() {
            // Given
            Long answerId = 404L;
            String sessionId = "session-real-404";
            Answer answer = mock(Answer.class);

            when(answerRepository.findByIdWithQuestion(answerId)).thenReturn(answer);
            when(answer.isOwnedBy(ACCOUNT_ID)).thenReturn(true);
            when(answer.getId()).thenReturn(answerId);
            when(answer.getType()).thenReturn(AnswerType.REAL_INTERVIEW);
            when(answer.getSessionId()).thenReturn(sessionId);
            when(answer.getStatus()).thenReturn(AnswerStatus.COMPLETED);
            when(answer.getContent()).thenReturn("실전 모드 답변");

            when(interviewSessionFeedbackQueryFlowService.getSessionFeedbackCompleted(ACCOUNT_ID, sessionId))
                    .thenReturn(new InterviewSessionFinalFeedbackResponse(
                            null,
                            null,
                            null,
                            sessionId,
                            "COMPLETED",
                            null,
                            List.of(),
                            null,
                            List.of(),
                            null,
                            List.of()
                    ));

            // When
            AnswerDetailResult result = answerQueryService.getDetail(ACCOUNT_ID, answerId);

            // Then
            assertThat(result.question()).isNull();
            assertThat(result.immediateFeedback()).isNull();
            assertThat(result.aiFeedback()).isNull();
            assertThat(result.sessionFinalFeedback()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 답변 조회 시 AnswerNotFoundException 발생")
        void getDetail_WithNonExistentAnswer_ShouldThrowException() {
            // Given
            Long answerId = 999L;
            when(answerRepository.findByIdWithQuestion(answerId)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> answerQueryService.getDetail(ACCOUNT_ID, answerId))
                    .isInstanceOf(AnswerNotFoundException.class);
        }
    }
}
