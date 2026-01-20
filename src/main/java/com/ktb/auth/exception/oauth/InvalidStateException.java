package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class InvalidStateException extends BusinessException {

    public InvalidStateException() {
        super(ErrorCode.INVALID_STATE, "state 검증에 실패했습니다");
    }
}
