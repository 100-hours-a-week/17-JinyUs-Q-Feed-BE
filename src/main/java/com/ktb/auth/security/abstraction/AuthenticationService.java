package com.ktb.auth.security.abstraction;

import java.util.Optional;

/**
 * 인증 서비스 추상화
 * - Spring Security에 독립적인 인증 로직
 * - 단위 테스트 가능
 */
public interface AuthenticationService {

    /**
     * 토큰을 검증하고 인증된 사용자 정보 반환
     *
     * @param token JWT 토큰
     * @return 인증된 사용자 (실패 시 Empty)
     */
    Optional<AuthenticatedUser> authenticate(String token);
}
