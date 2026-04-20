package com.ktb.ai.feedback.classifier;

import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;

public interface AiFeedbackOutcomeClassifier {

    FeedbackOutcome classify(AiFeedbackResponse response);
}
