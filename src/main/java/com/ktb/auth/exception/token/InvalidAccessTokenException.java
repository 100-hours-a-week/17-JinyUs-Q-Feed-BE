package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidAccessTokenException extends BusinessException {

    public InvalidAccessTokenException() {
        super(ErrorCode.INVALID_ACCESS_TOKEN);
    }

    public InvalidAccessTokenException(String reason) {
        super(ErrorCode.INVALID_ACCESS_TOKEN,
                String.format("%s: %s",ErrorCode.INVALID_ACCESS_TOKEN.getMessage() , reason));
    }
}
