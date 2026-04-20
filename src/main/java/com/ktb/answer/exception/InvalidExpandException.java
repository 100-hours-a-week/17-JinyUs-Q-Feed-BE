package com.ktb.answer.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidExpandException extends BusinessException {

    public InvalidExpandException(String value) {
        String errMsg = String.format("%s value={%s}",ErrorCode.INVALID_INPUT.getMessage() ,value);
        super(ErrorCode.INVALID_INPUT, errMsg);
    }
}
