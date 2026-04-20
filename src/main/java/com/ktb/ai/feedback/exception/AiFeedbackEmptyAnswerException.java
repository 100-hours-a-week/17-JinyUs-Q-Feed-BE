package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackEmptyAnswerException extends BusinessException {

    public AiFeedbackEmptyAnswerException() {
        super(ErrorCode.AI_FEEDBACK_EMPTY_ANSWER);
    }

    public AiFeedbackEmptyAnswerException(String message) {
        super(ErrorCode.AI_FEEDBACK_EMPTY_ANSWER, message);
    }
}
