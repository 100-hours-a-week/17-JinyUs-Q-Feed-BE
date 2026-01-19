package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException(String reason) {
        super(ErrorCode.INVALID_REFRESH_TOKEN,
                String.format("Refresh Token이 유효하지 않습니다: %s", reason));
    }
}
