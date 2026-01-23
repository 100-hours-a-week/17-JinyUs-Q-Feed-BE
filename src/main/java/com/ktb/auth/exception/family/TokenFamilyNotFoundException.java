package com.ktb.auth.exception.family;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class TokenFamilyNotFoundException extends BusinessException {

    public TokenFamilyNotFoundException(Long familyId) {
        super(ErrorCode.TOKEN_FAMILY_NOT_FOUND,
            String.format(
                "%s familyId=%d", ErrorCode.TOKEN_FAMILY_NOT_FOUND.getMessage(), familyId
            ));
    }

    public TokenFamilyNotFoundException(String familyUuid) {
        super(ErrorCode.TOKEN_FAMILY_NOT_FOUND,
            String.format("%s familyUuid=%s",ErrorCode.TOKEN_FAMILY_NOT_FOUND.getMessage() , familyUuid)
        );
    }
}
