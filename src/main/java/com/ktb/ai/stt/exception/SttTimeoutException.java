package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class SttTimeoutException extends BusinessException {

    public SttTimeoutException() {
        super(ErrorCode.STT_TIMEOUT, ErrorCode.STT_TIMEOUT.getMessage());
    }

    public SttTimeoutException(String message) {
        super(ErrorCode.STT_TIMEOUT, message);
    }

    public SttTimeoutException(Throwable cause) {
        super(ErrorCode.STT_TIMEOUT, ErrorCode.STT_TIMEOUT.getMessage(), cause);
    }
}
