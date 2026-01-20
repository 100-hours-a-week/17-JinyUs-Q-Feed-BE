package com.ktb.auth.security.abstraction;

/**
 * 요청 컨텍스트 추상화
 * - HttpServletRequest에 독립적
 * - 필요한 요청 정보만 노출
 */
public interface RequestContext {

    /**
     * HTTP 헤더 값 조회
     *
     * @param name 헤더 이름
     * @return 헤더 값
     */
    String getHeader(String name);
}
