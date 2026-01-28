package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidExchangeCodeException extends BusinessException {

    public InvalidExchangeCodeException() {
        super(ErrorCode.INVALID_EXCHANGE_CODE);
    }
}
