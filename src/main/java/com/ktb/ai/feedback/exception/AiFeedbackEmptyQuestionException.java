package com.ktb.ai.feedback.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AiFeedbackEmptyQuestionException extends BusinessException {

    public AiFeedbackEmptyQuestionException() {
        super(ErrorCode.AI_FEEDBACK_EMPTY_QUESTION);
    }

    public AiFeedbackEmptyQuestionException(String message) {
        super(ErrorCode.AI_FEEDBACK_EMPTY_QUESTION, message);
    }
}
