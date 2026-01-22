package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class SttServiceException extends BusinessException {

    public SttServiceException() {
        super(ErrorCode.STT_SERVICE_ERROR, ErrorCode.STT_SERVICE_ERROR.getMessage());
    }

    public SttServiceException(String message) {
        super(ErrorCode.STT_SERVICE_ERROR, message);
    }

    public SttServiceException(Throwable cause) {
        super(ErrorCode.STT_SERVICE_ERROR, ErrorCode.STT_SERVICE_ERROR.getMessage(), cause);
    }

    public SttServiceException(String message, Throwable cause) {
        super(ErrorCode.STT_SERVICE_ERROR, message, cause);
    }
}
