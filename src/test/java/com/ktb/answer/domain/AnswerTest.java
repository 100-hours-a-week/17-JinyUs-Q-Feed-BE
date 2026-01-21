package com.ktb.answer.domain;

import com.ktb.answer.exception.AnswerRequiredTypeException;
import com.ktb.answer.exception.InvalidAnswerStatusTransitionException;
import com.ktb.auth.domain.UserAccount;
import com.ktb.fixture.AnswerFixture;
import com.ktb.question.domain.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DisplayName("Answer 도메인 테스트")
class AnswerTest {

    private static final String VALID_CONTENT = "테스트 답변 내용입니다.";
    private static final int MAX_CONTENT_SIZE = 1500;

    @Nested
    @DisplayName("Answer 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 Answer 생성 시 성공")
        void create_WithValidData_ShouldSucceed() {
            // Given
            Question question = mock(Question.class);
            UserAccount account = mock(UserAccount.class);
            String content = VALID_CONTENT;
            AnswerType type = AnswerType.PRACTICE_INTERVIEW;

            // When
            Answer answer = Answer.create(question, account, content, type);

            // Then
            assertThat(answer).isNotNull();
            assertThat(answer.getQuestion()).isEqualTo(question);
            assertThat(answer.getAccount()).isEqualTo(account);
            assertThat(answer.getContent()).isEqualTo(content);
            assertThat(answer.getType()).isEqualTo(type);
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.SUBMITTED);
        }

        @Test
        @DisplayName("Type이 null이면 AnswerRequiredTypeException 발생")
        void create_WithNullType_ShouldThrowException() {
            // Given
            Question question = mock(Question.class);
            UserAccount account = mock(UserAccount.class);

            // When & Then
            assertThatThrownBy(() -> Answer.create(question, account, VALID_CONTENT, null))
                    .isInstanceOf(AnswerRequiredTypeException.class);
        }

        @Test
        @DisplayName("모든 AnswerType으로 생성 가능")
        void create_WithAllAnswerTypes_ShouldSucceed() {
            // Given
            Question question = mock(Question.class);
            UserAccount account = mock(UserAccount.class);

            // When & Then
            for (AnswerType type : AnswerType.values()) {
                Answer answer = Answer.create(question, account, VALID_CONTENT, type);
                assertThat(answer.getType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("초기 상태는 SUBMITTED")
        void create_ShouldHaveSubmittedStatus() {
            // Given
            Question question = mock(Question.class);
            UserAccount account = mock(UserAccount.class);

            // When
            Answer answer = Answer.create(question, account, VALID_CONTENT, AnswerType.PRACTICE_INTERVIEW);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.SUBMITTED);
        }
    }

    @Nested
    @DisplayName("Content 업데이트 테스트")
    class UpdateContentTest {

        @Test
        @DisplayName("유효한 content로 업데이트 성공")
        void updateContent_WithValidContent_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswer();
            String newContent = "새로운 답변 내용입니다.";

            // When
            answer.updateContent(newContent);

            // Then
            assertThat(answer.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("빈 문자열로 업데이트 가능 (검증은 Service에서)")
        void updateContent_WithEmptyString_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswer();

            // When
            answer.updateContent("");

            // Then
            assertThat(answer.getContent()).isEmpty();
        }

        @Test
        @DisplayName("1500자 content로 업데이트 가능")
        void updateContent_WithMaxLength_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswer();
            String maxContent = "A".repeat(MAX_CONTENT_SIZE);

            // When
            answer.updateContent(maxContent);

            // Then
            assertThat(answer.getContent()).hasSize(MAX_CONTENT_SIZE);
        }
    }

    @Nested
    @DisplayName("상태 전이 테스트")
    class TransitionTest {

        @Test
        @DisplayName("SUBMITTED → TRANSCRIBING 전이 성공")
        void transitionTo_FromSubmittedToTranscribing_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

            // When
            answer.transitionTo(AnswerStatus.TRANSCRIBING);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.TRANSCRIBING);
        }

        @Test
        @DisplayName("SUBMITTED → IMMEDIATE_FEEDBACK_READY 전이 성공")
        void transitionTo_FromSubmittedToImmediateFeedbackReady_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

            // When
            answer.transitionTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);
        }

        @Test
        @DisplayName("SUBMITTED → AI_FEEDBACK_PROCESSING 전이 성공")
        void transitionTo_FromSubmittedToAiFeedbackProcessing_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

            // When
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
        }

        @Test
        @DisplayName("SUBMITTED → FAILED 전이 성공")
        void transitionTo_FromSubmittedToFailed_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

            // When
            answer.transitionTo(AnswerStatus.FAILED);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.FAILED);
        }

        @Test
        @DisplayName("SUBMITTED → COMPLETED 직접 전이 불가")
        void transitionTo_FromSubmittedToCompleted_ShouldThrowException() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

            // When & Then
            assertThatThrownBy(() -> answer.transitionTo(AnswerStatus.COMPLETED))
                    .isInstanceOf(InvalidAnswerStatusTransitionException.class);
        }

        @Test
        @DisplayName("TRANSCRIBING → IMMEDIATE_FEEDBACK_READY 전이 성공")
        void transitionTo_FromTranscribingToImmediateFeedbackReady_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.TRANSCRIBING);

            // When
            answer.transitionTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);
        }

        @Test
        @DisplayName("TRANSCRIBING → FAILED 전이 성공")
        void transitionTo_FromTranscribingToFailed_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.TRANSCRIBING);

            // When
            answer.transitionTo(AnswerStatus.FAILED);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.FAILED);
        }

        @Test
        @DisplayName("IMMEDIATE_FEEDBACK_READY → AI_FEEDBACK_PROCESSING 전이 성공")
        void transitionTo_FromImmediateFeedbackReadyToAiFeedbackProcessing_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);

            // When
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
        }

        @Test
        @DisplayName("AI_FEEDBACK_PROCESSING → COMPLETED 전이 성공")
        void transitionTo_FromAiFeedbackProcessingToCompleted_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // When
            answer.transitionTo(AnswerStatus.COMPLETED);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.COMPLETED);
        }

        @Test
        @DisplayName("AI_FEEDBACK_PROCESSING → FAILED_RETRYABLE 전이 성공")
        void transitionTo_FromAiFeedbackProcessingToFailedRetryable_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // When
            answer.transitionTo(AnswerStatus.FAILED_RETRYABLE);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.FAILED_RETRYABLE);
        }

        @Test
        @DisplayName("FAILED_RETRYABLE → AI_FEEDBACK_PROCESSING 재시도 성공")
        void transitionTo_FromFailedRetryableToAiFeedbackProcessing_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
            answer.transitionTo(AnswerStatus.FAILED_RETRYABLE);

            // When
            answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // Then
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 다른 상태로 전이 불가")
        void transitionTo_FromCompletedToAnyStatus_ShouldThrowException() {
            // Given
            Answer answer = AnswerFixture.createCompletedAnswer();

            // When & Then
            assertThatThrownBy(() -> answer.transitionTo(AnswerStatus.SUBMITTED))
                    .isInstanceOf(InvalidAnswerStatusTransitionException.class);

            assertThatThrownBy(() -> answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING))
                    .isInstanceOf(InvalidAnswerStatusTransitionException.class);
        }

        @Test
        @DisplayName("FAILED 상태에서 다른 상태로 전이 불가")
        void transitionTo_FromFailedToAnyStatus_ShouldThrowException() {
            // Given
            Answer answer = AnswerFixture.createFailedAnswer();

            // When & Then
            assertThatThrownBy(() -> answer.transitionTo(AnswerStatus.SUBMITTED))
                    .isInstanceOf(InvalidAnswerStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("AI 피드백 설정 테스트")
    class SetAiFeedbackTest {

        @Test
        @DisplayName("AI 피드백 설정 시 상태가 COMPLETED로 변경")
        void setAiFeedback_ShouldChangeStatusToCompleted() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.AI_FEEDBACK_PROCESSING);
            String feedback = "답변이 논리적이고 명확합니다.";

            // When
            answer.setAiFeedback(feedback);

            // Then
            assertThat(answer.getAiFeedback()).isEqualTo(feedback);
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.COMPLETED);
        }

        @Test
        @DisplayName("빈 피드백도 설정 가능")
        void setAiFeedback_WithEmptyFeedback_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // When
            answer.setAiFeedback("");

            // Then
            assertThat(answer.getAiFeedback()).isEmpty();
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.COMPLETED);
        }

        @Test
        @DisplayName("null 피드백도 설정 가능")
        void setAiFeedback_WithNullFeedback_ShouldSucceed() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithStatus(AnswerStatus.AI_FEEDBACK_PROCESSING);

            // When
            answer.setAiFeedback(null);

            // Then
            assertThat(answer.getAiFeedback()).isNull();
            assertThat(answer.getStatus()).isEqualTo(AnswerStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("소유권 확인 테스트")
    class IsOwnedByTest {

        @Test
        @DisplayName("본인의 답변이면 true 반환")
        void isOwnedBy_WithOwner_ShouldReturnTrue() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithAccountId(1L);

            // When
            boolean result = answer.isOwnedBy(1L);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 사용자의 답변이면 false 반환")
        void isOwnedBy_WithNonOwner_ShouldReturnFalse() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithAccountId(1L);

            // When
            boolean result = answer.isOwnedBy(999L);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Account가 null이면 false 반환")
        void isOwnedBy_WithNullAccount_ShouldReturnFalse() {
            // Given
            Answer answer = AnswerFixture.createAnswerWithAccount(null);

            // When
            boolean result = answer.isOwnedBy(1L);

            // Then
            assertThat(result).isFalse();
        }
    }

}
