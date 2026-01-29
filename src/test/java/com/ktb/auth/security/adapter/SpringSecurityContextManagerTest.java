package com.ktb.auth.security.adapter;

import com.ktb.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * SpringSecurityContextManager 단위 테스트
 * - Spring Security 동작 검증
 */
@DisplayName("SpringSecurityContextManager 단위 테스트")
class SpringSecurityContextManagerTest {

    private SpringSecurityContextManager contextManager;

    @BeforeEach
    void setUp() {
        contextManager = new SpringSecurityContextManager();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증 정보 설정 성공")
    void setAuthentication_ShouldSetSecurityContext() {
        // given
        TokenService.TokenClaims claims = new TokenService.TokenClaims(1L, List.of("ROLE_USER"));
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        contextManager.setAuthentication(claims, request);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isInstanceOf(SecurityUserAccount.class);
    }

    @Test
    @DisplayName("인증 컨텍스트 초기화 성공")
    void clearAuthentication_ShouldClearSecurityContext() {
        // given
        TokenService.TokenClaims claims = new TokenService.TokenClaims(1L, List.of("ROLE_USER"));
        HttpServletRequest request = mock(HttpServletRequest.class);

        contextManager.setAuthentication(claims, request);

        // when
        contextManager.clearAuthentication();

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
    }
}
