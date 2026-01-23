package com.ktb.auth.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.exception.token.InvalidAccessTokenException;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import com.ktb.auth.service.TokenService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationService 단위 테스트 (DIP 적용)")
class JwtAuthenticationServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private JwtAuthenticationService authenticationService;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String EXPIRED_TOKEN = "expired.jwt.token";
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "user@example.com";
    private static final List<String> ROLES = List.of("ROLE_USER");

    @Test
    @DisplayName("유효한 JWT로 인증 시 AuthenticatedUser 반환")
    void authenticate_WithValidToken_ShouldReturnAuthenticatedUser() {
        // given
        TokenService.TokenClaims claims = new TokenService.TokenClaims(USER_ID, ROLES);
        UserAccount mockUser = mock(UserAccount.class);

        when(tokenService.validateAccessToken(VALID_TOKEN)).thenReturn(claims);
        when(userAccountRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.isActive()).thenReturn(true);
        when(mockUser.getId()).thenReturn(USER_ID);
        when(mockUser.getEmail()).thenReturn(EMAIL);

        // when
        Optional<AuthenticatedUser> result = authenticationService.authenticate(VALID_TOKEN);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(USER_ID);
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
        assertThat(result.get().getRoles()).isEqualTo(ROLES);
        assertThat(result.get().isActive()).isTrue();

        verify(tokenService).validateAccessToken(VALID_TOKEN);
        verify(userAccountRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("비활성 계정으로 인증 시 빈 Optional 반환")
    void authenticate_WithInactiveAccount_ShouldReturnEmpty() {
        // given
        TokenService.TokenClaims claims = new TokenService.TokenClaims(USER_ID, ROLES);
        UserAccount mockUser = mock(UserAccount.class);

        when(tokenService.validateAccessToken(VALID_TOKEN)).thenReturn(claims);
        when(userAccountRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.isActive()).thenReturn(false);

        // when
        Optional<AuthenticatedUser> result = authenticationService.authenticate(VALID_TOKEN);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 인증 시 빈 Optional 반환")
    void authenticate_WithNonExistentUser_ShouldReturnEmpty() {
        // given
        TokenService.TokenClaims claims = new TokenService.TokenClaims(USER_ID, ROLES);

        when(tokenService.validateAccessToken(VALID_TOKEN)).thenReturn(claims);
        when(userAccountRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // when
        Optional<AuthenticatedUser> result = authenticationService.authenticate(VALID_TOKEN);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("만료된 JWT로 인증 시 빈 Optional 반환")
    void authenticate_WithExpiredToken_ShouldReturnEmpty() {
        // given
        when(tokenService.validateAccessToken(EXPIRED_TOKEN))
                .thenThrow(new InvalidAccessTokenException("토큰 만료"));

        // when
        Optional<AuthenticatedUser> result = authenticationService.authenticate(EXPIRED_TOKEN);

        // then
        assertThat(result).isEmpty();
        verify(tokenService).validateAccessToken(EXPIRED_TOKEN);
        verify(userAccountRepository, never()).findById(any());
    }

    @Test
    @DisplayName("변조된 JWT로 인증 시 빈 Optional 반환")
    void authenticate_WithTamperedToken_ShouldReturnEmpty() {
        // given
        String tamperedToken = "tampered.jwt.token";
        when(tokenService.validateAccessToken(tamperedToken))
                .thenThrow(new InvalidAccessTokenException("잘못된 서명"));

        // when
        Optional<AuthenticatedUser> result = authenticationService.authenticate(tamperedToken);

        // then
        assertThat(result).isEmpty();
    }
}
