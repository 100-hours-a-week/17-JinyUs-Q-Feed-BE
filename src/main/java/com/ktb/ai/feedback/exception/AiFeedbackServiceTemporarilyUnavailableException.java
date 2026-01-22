package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackServiceTemporarilyUnavailableException extends BusinessException {

    public AiFeedbackServiceTemporarilyUnavailableException() {
        super(ErrorCode.AI_FEEDBACK_SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    public AiFeedbackServiceTemporarilyUnavailableException(String message) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_TEMPORARILY_UNAVAILABLE, message);
    }

    public AiFeedbackServiceTemporarilyUnavailableException(Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_TEMPORARILY_UNAVAILABLE,
                ErrorCode.AI_FEEDBACK_SERVICE_TEMPORARILY_UNAVAILABLE.getMessage(), cause);
    }

    public AiFeedbackServiceTemporarilyUnavailableException(String message, Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_TEMPORARILY_UNAVAILABLE, message, cause);
    }
}
