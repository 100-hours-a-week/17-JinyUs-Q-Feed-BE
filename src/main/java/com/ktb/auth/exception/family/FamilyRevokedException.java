package com.ktb.auth.exception.family;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FamilyRevokedException extends BusinessException {

    public FamilyRevokedException() {
        super(ErrorCode.FAMILY_REVOKED);
    }

    public FamilyRevokedException(Long familyId) {
        super(ErrorCode.FAMILY_REVOKED,
            String.format("%s: familyId=%d",ErrorCode.FAMILY_REVOKED.getMessage() , familyId)
        );
    }
}
