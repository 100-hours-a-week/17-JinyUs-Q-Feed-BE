package com.ktb.auth.exception.account;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(Long accountId) {
        super(ErrorCode.ACCOUNT_NOT_FOUND,
            String.format(
                "%ss accountId=%d",ErrorCode.ACCOUNT_NOT_FOUND.getCode() , accountId
            ));
    }
}
