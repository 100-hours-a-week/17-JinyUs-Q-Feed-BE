package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackInternalServerException extends BusinessException {

    public AiFeedbackInternalServerException() {
        super(ErrorCode.AI_FEEDBACK_INTERNAL_SERVER_ERROR);
    }

    public AiFeedbackInternalServerException(String message) {
        super(ErrorCode.AI_FEEDBACK_INTERNAL_SERVER_ERROR, message);
    }

    public AiFeedbackInternalServerException(Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_INTERNAL_SERVER_ERROR,
                ErrorCode.AI_FEEDBACK_INTERNAL_SERVER_ERROR.getMessage(), cause);
    }

    public AiFeedbackInternalServerException(String message, Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_INTERNAL_SERVER_ERROR, message, cause);
    }
}
