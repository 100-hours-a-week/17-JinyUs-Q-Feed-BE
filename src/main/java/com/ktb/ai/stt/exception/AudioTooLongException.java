package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AudioTooLongException extends BusinessException {

    public AudioTooLongException() {
        super(ErrorCode.AUDIO_TOO_LONG, ErrorCode.AUDIO_TOO_LONG.getMessage());
    }

    public AudioTooLongException(String message) {
        super(ErrorCode.AUDIO_TOO_LONG, message);
    }
}
