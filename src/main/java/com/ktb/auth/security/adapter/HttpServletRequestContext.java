package com.ktb.auth.security.adapter;

import com.ktb.auth.security.abstraction.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * HTTP Servlet Request 컨텍스트 어댑터
 * - HttpServletRequest를 RequestContext로 변환
 */
@Getter
@RequiredArgsConstructor
public class HttpServletRequestContext implements RequestContext {

    private final HttpServletRequest request;

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }
}
