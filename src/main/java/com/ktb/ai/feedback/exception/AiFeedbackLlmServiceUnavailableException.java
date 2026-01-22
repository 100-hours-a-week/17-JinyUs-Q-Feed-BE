package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackLlmServiceUnavailableException extends BusinessException {

    public AiFeedbackLlmServiceUnavailableException() {
        super(ErrorCode.AI_FEEDBACK_LLM_SERVICE_UNAVAILABLE);
    }

    public AiFeedbackLlmServiceUnavailableException(String message) {
        super(ErrorCode.AI_FEEDBACK_LLM_SERVICE_UNAVAILABLE, message);
    }

    public AiFeedbackLlmServiceUnavailableException(Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_LLM_SERVICE_UNAVAILABLE,
                ErrorCode.AI_FEEDBACK_LLM_SERVICE_UNAVAILABLE.getMessage(), cause);
    }

    public AiFeedbackLlmServiceUnavailableException(String message, Throwable cause) {
        super(ErrorCode.AI_FEEDBACK_LLM_SERVICE_UNAVAILABLE, message, cause);
    }
}
