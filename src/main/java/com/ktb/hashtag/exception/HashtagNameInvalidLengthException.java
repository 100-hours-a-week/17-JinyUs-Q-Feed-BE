package com.ktb.hashtag.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class HashtagNameInvalidLengthException extends BusinessException {
    public HashtagNameInvalidLengthException() {
        super(ErrorCode.HASHTAG_NAME_TOO_LONG);
    }
}
