package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidAccessTokenException extends BusinessException {

    public InvalidAccessTokenException(String reason) {
        super(ErrorCode.INVALID_ACCESS_TOKEN,
                String.format("Access Token이 유효하지 않습니다: %s", reason));
    }
}
