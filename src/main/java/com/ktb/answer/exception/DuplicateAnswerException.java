package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class DuplicateAnswerException extends BusinessException {

    public DuplicateAnswerException(String sessionId, Long questionId) {
        super(ErrorCode.DUPLICATE_ANSWER,
              String.format("%s: sessionId=%s, questionId=%d",
                      ErrorCode.DUPLICATE_ANSWER.getMessage(), sessionId, questionId));
    }
}
