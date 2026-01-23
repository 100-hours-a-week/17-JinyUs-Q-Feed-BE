package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionInvalidContentException extends BusinessException {

    public QuestionInvalidContentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
