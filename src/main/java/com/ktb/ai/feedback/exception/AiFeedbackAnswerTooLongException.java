package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackAnswerTooLongException extends BusinessException {

    public AiFeedbackAnswerTooLongException() {
        super(ErrorCode.AI_FEEDBACK_ANSWER_TOO_LONG);
    }

    public AiFeedbackAnswerTooLongException(String message) {
        super(ErrorCode.AI_FEEDBACK_ANSWER_TOO_LONG, message);
    }
}
