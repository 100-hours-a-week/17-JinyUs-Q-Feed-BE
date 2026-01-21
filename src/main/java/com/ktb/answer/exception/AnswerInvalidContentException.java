package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AnswerInvalidContentException extends BusinessException {

    public AnswerInvalidContentException() {
        super(ErrorCode.ANSWER_INVALID_CONTENT);
    }
}
