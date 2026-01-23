package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionRequiredTypeException extends BusinessException {
    public QuestionRequiredTypeException() {
        super(ErrorCode.QUESTION_TYPE_REQUIRED);
    }
}
