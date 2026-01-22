package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AnswerNotFoundException extends BusinessException {

    public AnswerNotFoundException() {
        super(ErrorCode.ANSWER_NOT_FOUND);
    }

    public AnswerNotFoundException(Long answerId) {
        String errMsg = String.format("%s answerId = %d", ErrorCode.ANSWER_NOT_FOUND.getMessage(), answerId);
        super(ErrorCode.ANSWER_NOT_FOUND, errMsg);
    }
}
