package com.ktb.auth.exception.family;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FamilyOwnershipException extends BusinessException {

    public FamilyOwnershipException() {
        super(ErrorCode.FAMILY_OWNERSHIP_MISMATCH);
    }
    public FamilyOwnershipException(Long familyId, Long accountId) {
        super(ErrorCode.FAMILY_OWNERSHIP_MISMATCH,
                String.format("%s: familyId=%d, accountId=%d",ErrorCode.FAMILY_OWNERSHIP_MISMATCH.getMessage() , familyId, accountId));
    }
}
