package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackAnswerTooShortException extends BusinessException {

    public AiFeedbackAnswerTooShortException() {
        super(ErrorCode.AI_FEEDBACK_ANSWER_TOO_SHORT);
    }

    public AiFeedbackAnswerTooShortException(String message) {
        super(ErrorCode.AI_FEEDBACK_ANSWER_TOO_SHORT, message);
    }
}
