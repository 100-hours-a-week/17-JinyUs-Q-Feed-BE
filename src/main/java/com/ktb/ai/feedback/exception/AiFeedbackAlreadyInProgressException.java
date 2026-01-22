package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackAlreadyInProgressException extends BusinessException {

    public AiFeedbackAlreadyInProgressException() {
        super(ErrorCode.AI_FEEDBACK_ALREADY_IN_PROGRESS);
    }

    public AiFeedbackAlreadyInProgressException(String message) {
        super(ErrorCode.AI_FEEDBACK_ALREADY_IN_PROGRESS, message);
    }
}
