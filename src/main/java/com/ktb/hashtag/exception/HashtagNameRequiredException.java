package com.ktb.hashtag.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class HashtagNameRequiredException extends BusinessException {
    public HashtagNameRequiredException() {
        super(ErrorCode.HASHTAG_NAME_REQUIRED);
    }
}
