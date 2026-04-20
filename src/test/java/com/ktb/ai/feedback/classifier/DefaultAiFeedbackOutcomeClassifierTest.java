package com.ktb.ai.feedback.classifier;

import com.ktb.ai.feedback.dto.response.AiFeedbackBadCaseFeedback;
import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("DefaultAiFeedbackOutcomeClassifier 단위 테스트")
class DefaultAiFeedbackOutcomeClassifierTest {

    private final DefaultAiFeedbackOutcomeClassifier classifier = new DefaultAiFeedbackOutcomeClassifier();

    @Test
    @DisplayName("badCaseFeedback이 null이면 NORMAL을 반환한다")
    void classify_badCaseFeedbackNull_returnsNormal() {
        AiFeedbackResponse response = new AiFeedbackResponse(
            null, null, null, null, null, null, null, null, null
        );

        FeedbackOutcome outcome = classifier.classify(response);

        assertThat(outcome).isEqualTo(FeedbackOutcome.NORMAL);
    }

    @Test
    @DisplayName("badCaseFeedback이 존재하면 BAD_CASE를 반환한다")
    void classify_badCaseFeedbackPresent_returnsBadCase() {
        AiFeedbackBadCaseFeedback badCaseFeedback = mock(AiFeedbackBadCaseFeedback.class);
        AiFeedbackResponse response = new AiFeedbackResponse(
            null, null, null, null, null, null, badCaseFeedback, null, null
        );

        FeedbackOutcome outcome = classifier.classify(response);

        assertThat(outcome).isEqualTo(FeedbackOutcome.BAD_CASE);
    }
}
