package com.ktb.answer.dto;

import com.ktb.answer.domain.AnswerType;

public record AnswerDetailResult(
        Long answerId,
        AnswerType type,
        QuestionSummary question,
        AnswerContentResult answer,
        ImmediateFeedbackResult immediateFeedback,
        AiFeedbackSummary aiFeedback
) {
}
