package com.ktb.answer.exception;

import com.ktb.answer.domain.AnswerStatus;
import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidAnswerStatusTransitionException extends BusinessException {

    public InvalidAnswerStatusTransitionException(AnswerStatus from, AnswerStatus to) {
        super(ErrorCode.ANSWER_INVALID_STATUS_TRANSITION,
              String.format("%s: %s → %s",
                      ErrorCode.ANSWER_INVALID_STATUS_TRANSITION.getMessage(), from, to));
    }
}
