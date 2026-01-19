package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidAuthorizationCodeException extends BusinessException {

    public InvalidAuthorizationCodeException() {
        super(ErrorCode.INVALID_AUTHORIZATION_CODE, "authorization code가 유효하지 않습니다");
    }
}
