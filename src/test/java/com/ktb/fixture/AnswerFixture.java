package com.ktb.fixture;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.auth.domain.UserAccount;
import com.ktb.question.domain.Question;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnswerFixture {

    private static final String DEFAULT_CONTENT = "테스트 답변 내용입니다.";
    private static final AnswerType DEFAULT_TYPE = AnswerType.PRACTICE_INTERVIEW;
    private static final int MAX_CONTENT_SIZE = 1500;

    public static Answer createAnswer() {
        return Answer.create(
                mock(Question.class),
                mock(UserAccount.class),
                DEFAULT_CONTENT,
                DEFAULT_TYPE
        );
    }

    public static Answer createAnswerWithContent(String content) {
        return Answer.create(
                mock(Question.class),
                mock(UserAccount.class),
                content,
                DEFAULT_TYPE
        );
    }

    public static Answer createAnswerWithType(AnswerType type) {
        return Answer.create(
                mock(Question.class),
                mock(UserAccount.class),
                DEFAULT_CONTENT,
                type
        );
    }

    public static Answer createAnswerWithAccount(UserAccount account) {
        return Answer.create(
                mock(Question.class),
                account,
                DEFAULT_CONTENT,
                DEFAULT_TYPE
        );
    }

    public static Answer createAnswerWithAccountId(Long accountId) {
        UserAccount account = mock(UserAccount.class);
        when(account.getId()).thenReturn(accountId);

        return Answer.create(
                mock(Question.class),
                account,
                DEFAULT_CONTENT,
                DEFAULT_TYPE
        );
    }

    public static Answer createAnswerWithQuestion(Question question) {
        return Answer.create(
                question,
                mock(UserAccount.class),
                DEFAULT_CONTENT,
                DEFAULT_TYPE
        );
    }

    public static Answer createAnswerWithMaxContent() {
        String maxContent = "A".repeat(MAX_CONTENT_SIZE);
        return createAnswerWithContent(maxContent);
    }

    public static String createContentExceedingMaxLength() {
        return "A".repeat(MAX_CONTENT_SIZE + 1);
    }

    public static Answer createAnswerWithStatus(AnswerStatus targetStatus) {
        Answer answer = createAnswer();
        transitionToStatus(answer, targetStatus);
        return answer;
    }

    public static Answer createAnswerWithAiFeedback(String feedback) {
        Answer answer = createAnswer();
        answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
        answer.setAiFeedback(feedback);
        return answer;
    }

    public static Answer createCompletedAnswer() {
        return createAnswerWithAiFeedback("AI 피드백 내용입니다.");
    }

    public static Answer createFailedAnswer() {
        Answer answer = createAnswer();
        answer.transitionTo(AnswerStatus.FAILED);
        return answer;
    }

    public static Answer createRetryableFailedAnswer() {
        Answer answer = createAnswer();
        answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
        answer.transitionTo(AnswerStatus.FAILED_RETRYABLE);
        return answer;
    }

    public static Answer createAnswer(Question question, UserAccount account, String content, AnswerType type) {
        return Answer.create(question, account, content, type);
    }

    private static void transitionToStatus(Answer answer, AnswerStatus targetStatus) {
        switch (targetStatus) {
            case SUBMITTED:
                // 기본 상태, 아무 작업 불필요
                break;

            case TRANSCRIBING:
                answer.transitionTo(AnswerStatus.TRANSCRIBING);
                break;

            case IMMEDIATE_FEEDBACK_READY:
                answer.transitionTo(AnswerStatus.IMMEDIATE_FEEDBACK_READY);
                break;

            case AI_FEEDBACK_PROCESSING:
                answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
                break;

            case COMPLETED:
                answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
                answer.setAiFeedback("AI 피드백");
                break;

            case FAILED_RETRYABLE:
                answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
                answer.transitionTo(AnswerStatus.FAILED_RETRYABLE);
                break;

            case FAILED:
                answer.transitionTo(AnswerStatus.FAILED);
                break;

            default:
                throw new IllegalArgumentException("지원되지 않는 상태: " + targetStatus);
        }
    }
}
