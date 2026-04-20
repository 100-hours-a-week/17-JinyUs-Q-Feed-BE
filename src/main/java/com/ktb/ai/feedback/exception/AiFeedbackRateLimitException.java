package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackRateLimitException extends BusinessException {

    public AiFeedbackRateLimitException() {
        super(ErrorCode.AI_FEEDBACK_RATE_LIMIT_EXCEEDED);
    }

    public AiFeedbackRateLimitException(String message) {
        super(ErrorCode.AI_FEEDBACK_RATE_LIMIT_EXCEEDED, message);
    }
}
