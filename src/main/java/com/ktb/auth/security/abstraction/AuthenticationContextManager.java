package com.ktb.auth.security.abstraction;

/**
 * 인증 컨텍스트 관리 추상화
 * - SecurityContextHolder에 독립적
 * - 프레임워크별 인증 컨텍스트 관리
 */
public interface AuthenticationContextManager {

    /**
     * 인증 정보를 컨텍스트에 설정
     *
     * @param user 인증된 사용자
     * @param request 요청 컨텍스트
     */
    void setAuthentication(AuthenticatedUser user, RequestContext request);

    /**
     * 인증 컨텍스트 초기화
     */
    void clearAuthentication();
}
