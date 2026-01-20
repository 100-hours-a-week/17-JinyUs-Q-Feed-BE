package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class OAuthProviderException extends BusinessException {

    public OAuthProviderException(String provider, String reason) {
        super(ErrorCode.OAUTH_PROVIDER_ERROR,
                String.format("OAuth 제공자 통신에 실패했습니다: provider=%s, reason=%s", provider, reason));
    }
}
