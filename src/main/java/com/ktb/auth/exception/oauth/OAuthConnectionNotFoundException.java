package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class OAuthConnectionNotFoundException extends BusinessException {

    public OAuthConnectionNotFoundException(Long oauthId) {
        super(ErrorCode.OAUTH_CONNECTION_NOT_FOUND,
            String.format("%s oauthId=%d", ErrorCode.OAUTH_CONNECTION_NOT_FOUND.getMessage(),
                oauthId)
        );
    }
}
