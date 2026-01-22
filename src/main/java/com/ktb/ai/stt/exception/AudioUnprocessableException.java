package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AudioUnprocessableException extends BusinessException {

    public AudioUnprocessableException() {
        super(ErrorCode.AUDIO_UNPROCESSABLE, ErrorCode.AUDIO_UNPROCESSABLE.getMessage());
    }

    public AudioUnprocessableException(String message) {
        super(ErrorCode.AUDIO_UNPROCESSABLE, message);
    }
}
