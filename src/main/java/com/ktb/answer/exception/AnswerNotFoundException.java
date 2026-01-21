package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AnswerNotFoundException extends BusinessException {

    public AnswerNotFoundException() {
        super(ErrorCode.ANSWER_NOT_FOUND);
    }
}
