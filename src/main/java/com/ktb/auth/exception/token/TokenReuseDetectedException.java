package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class TokenReuseDetectedException extends BusinessException {

    public TokenReuseDetectedException(Long familyId) {
        super(ErrorCode.TOKEN_REUSE_DETECTED,
                String.format("%s Family 폐기됨: %d",ErrorCode.TOKEN_REUSE_DETECTED , familyId));
    }
}
