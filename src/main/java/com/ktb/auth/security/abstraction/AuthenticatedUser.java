package com.ktb.auth.security.abstraction;

import java.util.List;

/**
 * 인증된 사용자 추상화
 * - Spring Security UserDetails에 독립적
 * - 비즈니스 로직에서 사용하는 사용자 정보
 */
public interface AuthenticatedUser {

    /**
     * 사용자 ID
     */
    Long getUserId();

    /**
     * 이메일
     */
    String getEmail();

    /**
     * 권한 목록
     */
    List<String> getRoles();

    /**
     * 활성 상태 여부
     */
    boolean isActive();
}
