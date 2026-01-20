package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionNotFoundException extends BusinessException {

    public QuestionNotFoundException(Long questionId) {
        super(ErrorCode.QUESTION_NOT_FOUND,
                String.format("질문을 찾을 수 없습니다: %d", questionId));
    }
}
