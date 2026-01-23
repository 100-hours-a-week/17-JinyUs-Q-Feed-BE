package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionRequiredCategoryException extends BusinessException {
    public QuestionRequiredCategoryException() {
        super(ErrorCode.QUESTION_CATEGORY_REQUIRED);
    }
}
