package com.ktb.auth.security.service;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.exception.account.AccountNotFoundException;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import com.ktb.auth.security.abstraction.AuthenticationService;
import com.ktb.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationService implements AuthenticationService {

    private final TokenService tokenService;
    private final UserAccountRepository userAccountRepository;

    @Override
    public Optional<AuthenticatedUser> authenticate(String token) {
        try {
            TokenService.TokenClaims claims = tokenService.validateAccessToken(token);

            UserAccount account = userAccountRepository.findById(claims.userId())
                    .orElseThrow(() -> new AccountNotFoundException(claims.userId()));

            if (!account.isActive()) {
                log.warn("JWT 인증 실패: 비활성 계정 userId={}", claims.userId());
                return Optional.empty();
            }

            AuthenticatedUser authenticatedUser = new AuthenticatedUserAdapter(account, claims.roles());

            log.debug("JWT 인증 성공: userId={}, email={}", account.getId(), account.getEmail());

            return Optional.of(authenticatedUser);

        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
