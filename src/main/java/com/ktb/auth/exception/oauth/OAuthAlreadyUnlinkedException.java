package com.ktb.auth.exception.oauth;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class OAuthAlreadyUnlinkedException extends BusinessException {

    public OAuthAlreadyUnlinkedException(Long oauthId) {
        super(ErrorCode.OAUTH_ALREADY_UNLINKED,
            String.format("%s oauthId=%d", ErrorCode.OAUTH_ALREADY_UNLINKED, oauthId)
        );
    }
}
