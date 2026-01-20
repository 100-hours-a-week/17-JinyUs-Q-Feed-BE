package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionInvalidContentException extends BusinessException {

    public QuestionInvalidContentException(String detail) {
        super(ErrorCode.QUESTION_INVALID_CONTENT, detail);
    }
}
