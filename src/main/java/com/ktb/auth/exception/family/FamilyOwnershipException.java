package com.ktb.auth.exception.family;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FamilyOwnershipException extends BusinessException {

    public FamilyOwnershipException(Long familyId, Long accountId) {
        super(ErrorCode.FAMILY_OWNERSHIP_MISMATCH,
                String.format("세션 소유권이 일치하지 않습니다: familyId=%d, accountId=%d", familyId, accountId));
    }
}
