package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class MissingRefreshTokenException extends BusinessException {

    public MissingRefreshTokenException() {
        super(ErrorCode.MISSING_REFRESH_TOKEN);
    }
}
