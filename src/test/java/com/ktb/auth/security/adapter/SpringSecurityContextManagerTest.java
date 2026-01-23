package com.ktb.auth.security.adapter;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import com.ktb.auth.security.abstraction.RequestContext;
import com.ktb.auth.security.service.AuthenticatedUserAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        UserAccount mockUser = mock(UserAccount.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("user@example.com");

        AuthenticatedUser user = new AuthenticatedUserAdapter(mockUser, List.of("ROLE_USER"));
        RequestContext request = header -> "Bearer token";

        // when
        contextManager.setAuthentication(user, request);

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
        UserAccount mockUser = mock(UserAccount.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("user@example.com");

        AuthenticatedUser user = new AuthenticatedUserAdapter(mockUser, List.of("ROLE_USER"));
        RequestContext request = header -> "Bearer token";

        contextManager.setAuthentication(user, request);

        // when
        contextManager.clearAuthentication();

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
    }
}
