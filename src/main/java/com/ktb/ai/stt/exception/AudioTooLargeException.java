package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AudioTooLargeException extends BusinessException {

    public AudioTooLargeException() {
        super(ErrorCode.AUDIO_TOO_LARGE, ErrorCode.AUDIO_TOO_LARGE.getMessage());
    }

    public AudioTooLargeException(String message) {
        super(ErrorCode.AUDIO_TOO_LARGE, message);
    }
}
