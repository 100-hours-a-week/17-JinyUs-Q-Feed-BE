package com.ktb.ai.stt.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class AudioNotFoundException extends BusinessException {

    public AudioNotFoundException() {
        super(ErrorCode.AUDIO_NOT_FOUND, ErrorCode.AUDIO_NOT_FOUND.getMessage());
    }

    public AudioNotFoundException(String audioUrl) {
        super(
            ErrorCode.AUDIO_NOT_FOUND,
            String.format("%s: %s", ErrorCode.AUDIO_NOT_FOUND.getCode(), audioUrl)
        );
    }
}
