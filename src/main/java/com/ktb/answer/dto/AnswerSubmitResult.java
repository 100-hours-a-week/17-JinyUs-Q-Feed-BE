package com.ktb.answer.dto;

public record AnswerSubmitResult(
        Long answerId,
        String transcribedText,
        String audioUrl,
        ImmediateFeedbackResult immediateFeedback,
        FeedbackStatus aiFeedbackStatus
) {

    private static final FeedbackStatus DEFAULT_FEEDBACK_STATUS = FeedbackStatus.PROCESSING;

    public static AnswerSubmitResult processing(Long answerId, ImmediateFeedbackResult feedback) {
        return new AnswerSubmitResult(answerId, null, null, feedback, DEFAULT_FEEDBACK_STATUS);
    }
}
