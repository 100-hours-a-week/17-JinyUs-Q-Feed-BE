package com.ktb.auth.security.service;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import com.ktb.auth.security.abstraction.AuthenticationService;
import com.ktb.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * JWT 인증 서비스 구현
 * - Spring Security에 독립적인 순수 비즈니스 로직
 * - 단위 테스트 가능
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationService implements AuthenticationService {

    private final TokenService tokenService;
    private final UserAccountRepository userAccountRepository;

    @Override
    public Optional<AuthenticatedUser> authenticate(String token) {
        try {
            // 1. JWT 검증
            TokenService.TokenClaims claims = tokenService.validateAccessToken(token);

            // 2. 사용자 조회
            UserAccount account = userAccountRepository.findById(claims.userId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "사용자를 찾을 수 없습니다: " + claims.userId()));

            // 3. 활성 상태 확인
            if (!account.isActive()) {
                throw new IllegalStateException("비활성 계정입니다");
            }

            // 4. 인증된 사용자로 변환
            AuthenticatedUser authenticatedUser = new AuthenticatedUserAdapter(account, claims.roles());

            log.debug("JWT 인증 성공: userId={}, email={}", account.getId(), account.getEmail());

            return Optional.of(authenticatedUser);

        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
