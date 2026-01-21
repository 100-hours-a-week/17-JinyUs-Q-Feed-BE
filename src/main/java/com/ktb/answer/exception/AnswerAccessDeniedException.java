package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AnswerAccessDeniedException extends BusinessException {

    public AnswerAccessDeniedException(Long answerId, Long accountId) {
        super(ErrorCode.ANSWER_ACCESS_DENIED,
              String.format("%s: answerId=%d, accountId=%d",
                      ErrorCode.ANSWER_ACCESS_DENIED.getMessage(), answerId, accountId));
    }
}
