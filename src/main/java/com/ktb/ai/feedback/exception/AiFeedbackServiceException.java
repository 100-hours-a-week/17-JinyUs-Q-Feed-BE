package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackServiceException extends BusinessException {

    public AiFeedbackServiceException() {
        super(ErrorCode.AI_FEEDBACK_SERVICE_ERROR);
    }

    public AiFeedbackServiceException(String message) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_ERROR, message);
    }

    public AiFeedbackServiceException(Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_ERROR, ErrorCode.AI_FEEDBACK_SERVICE_ERROR.getMessage(), cause);
    }

    public AiFeedbackServiceException(String message, Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_SERVICE_ERROR, message, cause);
    }
}
