package com.ktb.auth.exception.token;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class TokenReuseDetectedException extends BusinessException {

    public TokenReuseDetectedException(Long familyId) {
        super(ErrorCode.TOKEN_REUSE_DETECTED,
                String.format("토큰 재사용이 탐지되었습니다. Family 폐기됨: %d", familyId));
    }
}
