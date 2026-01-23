package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class TokenHashingFailedException extends BusinessException {

    public TokenHashingFailedException(String reason) {
        super(ErrorCode.TOKEN_HASHING_FAILED, "토큰 해시 생성에 실패했습니다. reason=" + reason);
    }

    public TokenHashingFailedException(Throwable cause) {
        super(ErrorCode.TOKEN_HASHING_FAILED, "토큰 해시 생성에 실패했습니다.", cause);
    }
}
