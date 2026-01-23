package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(String reason) {
        super(ErrorCode.INVALID_REFRESH_TOKEN,
                String.format("%s: %s", ErrorCode.INVALID_REFRESH_TOKEN.getMessage(), reason));
    }
}
