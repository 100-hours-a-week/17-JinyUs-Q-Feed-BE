package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class TokenHashingFailedException extends BusinessException {

    public TokenHashingFailedException() {
        super(ErrorCode.TOKEN_HASHING_FAILED);
    }
}
