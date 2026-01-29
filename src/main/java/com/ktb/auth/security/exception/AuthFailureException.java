package com.ktb.auth.security.exception;

import com.ktb.common.domain.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthFailureException extends AuthenticationException {

    private final ErrorCode errorCode;

    public AuthFailureException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
