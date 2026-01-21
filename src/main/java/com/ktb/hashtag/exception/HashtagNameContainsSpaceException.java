package com.ktb.hashtag.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class HashtagNameContainsSpaceException extends BusinessException {
    public HashtagNameContainsSpaceException() {
        super(ErrorCode.HASHTAG_NAME_CONTAINS_SPACE);
    }
}
