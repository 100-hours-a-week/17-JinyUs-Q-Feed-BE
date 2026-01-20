package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class QuestionAlreadyDeletedException extends BusinessException {

    public QuestionAlreadyDeletedException(Long questionId) {
        super(ErrorCode.QUESTION_ALREADY_DELETED,
                String.format("이미 삭제된 질문입니다: %d", questionId));
    }
}
