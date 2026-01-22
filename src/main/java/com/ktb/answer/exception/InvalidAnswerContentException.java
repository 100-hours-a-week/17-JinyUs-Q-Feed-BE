package com.ktb.answer.exception;

import static com.ktb.common.domain.ErrorCode.ANSWER_INVALID_CONTENT;

import com.ktb.common.exception.BusinessException;

public class InvalidAnswerContentException extends BusinessException {
    public InvalidAnswerContentException(Long contentSize) {
        String errMsg = String.format("%s content string length : %d", ANSWER_INVALID_CONTENT.getMessage(), contentSize);
        super(ANSWER_INVALID_CONTENT, errMsg);
    }
}
