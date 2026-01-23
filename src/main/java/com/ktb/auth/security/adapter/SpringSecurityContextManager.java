package com.ktb.auth.security.adapter;

import com.ktb.auth.exception.security.InvalidAuthenticatedUserException;
import com.ktb.auth.security.abstraction.AuthenticatedUser;
import com.ktb.auth.security.abstraction.AuthenticationContextManager;
import com.ktb.auth.security.abstraction.RequestContext;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import com.ktb.auth.security.service.AuthenticatedUserAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

/**
 * Spring Security 컨텍스트 관리 어댑터
 * - Spring Security의 SecurityContextHolder를 사용하여 인증 정보 관리
 * - 프레임워크 종속 코드를 이 클래스로 격리
 */
@Component
@Slf4j
public class SpringSecurityContextManager implements AuthenticationContextManager {

    @Override
    public void setAuthentication(AuthenticatedUser user, RequestContext request) {
        // SecurityUserAccount 생성
        SecurityUserAccount securityUser = new SecurityUserAccount(
                getUnderlyingAccount(user)
        );

        // Spring Security Authentication 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        securityUser.getAuthorities()
                );

        // Details 설정 (HttpServletRequest가 있는 경우)
        if (request instanceof HttpServletRequestContext httpContext) {
            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(httpContext.getRequest())
            );
        }

        // SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Spring Security 인증 컨텍스트 설정: userId={}", user.getUserId());
    }

    @Override
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
        log.debug("Spring Security 인증 컨텍스트 초기화");
    }

    private com.ktb.auth.domain.UserAccount getUnderlyingAccount(AuthenticatedUser user) {
        if (user instanceof AuthenticatedUserAdapter adapter) {
            return adapter.getAccount();
        }
        throw new InvalidAuthenticatedUserException(user.getClass().getName());
    }
}
