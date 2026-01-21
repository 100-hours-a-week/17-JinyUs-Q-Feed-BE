package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AnswerRequiredTypeException extends BusinessException {
    public AnswerRequiredTypeException() {
        super(ErrorCode.ANSWER_TYPE_REQUIRED);
    }
}
