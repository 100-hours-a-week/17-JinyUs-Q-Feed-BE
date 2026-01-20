package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class UnsupportedProviderException extends BusinessException {

    public UnsupportedProviderException(String provider) {
        super(ErrorCode.UNSUPPORTED_PROVIDER,
                String.format("지원하지 않는 OAuth 제공자입니다: %s", provider));
    }
}
