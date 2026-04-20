package com.ktb.ai.feedback.classifier;

import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;
import org.springframework.stereotype.Component;

@Component
public class DefaultAiFeedbackOutcomeClassifier implements AiFeedbackOutcomeClassifier {

    @Override
    public FeedbackOutcome classify(AiFeedbackResponse response) {
        return response.badCaseFeedback() != null ? FeedbackOutcome.BAD_CASE : FeedbackOutcome.NORMAL;
    }
}
