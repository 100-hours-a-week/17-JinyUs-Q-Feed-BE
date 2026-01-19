package com.ktb.auth.exception.family;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class FamilyRevokedException extends BusinessException {

    public FamilyRevokedException(Long familyId) {
        super(ErrorCode.FAMILY_REVOKED,
                String.format("세션이 이미 종료되었습니다: familyId=%d", familyId));
    }
}
