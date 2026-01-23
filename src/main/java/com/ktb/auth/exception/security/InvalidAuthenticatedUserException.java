package com.ktb.auth.exception.security;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidAuthenticatedUserException extends BusinessException {

    public InvalidAuthenticatedUserException(String userType) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, "지원하지 않는 인증 사용자 타입입니다. type=" + userType);
    }
}
