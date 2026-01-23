package com.ktb.auth.exception.account;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AccountInvalidNicknameException extends BusinessException {

    public AccountInvalidNicknameException() {
        super(ErrorCode.ACCOUNT_INVALID_NICKNAME);
    }
}
