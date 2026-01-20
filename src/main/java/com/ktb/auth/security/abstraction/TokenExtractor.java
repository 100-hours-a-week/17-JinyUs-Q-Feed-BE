package com.ktb.auth.security.abstraction;

import java.util.Optional;

/**
 * 토큰 추출 추상화
 * - HTTP 요청에서 토큰을 추출하는 책임
 * - 다양한 추출 방식 지원 (Bearer, Cookie, Query 등)
 */
public interface TokenExtractor {

    /**
     * 요청 컨텍스트에서 토큰 추출
     *
     * @param request 요청 컨텍스트
     * @return 추출된 토큰 (없으면 Empty)
     */
    Optional<String> extractToken(RequestContext request);
}
