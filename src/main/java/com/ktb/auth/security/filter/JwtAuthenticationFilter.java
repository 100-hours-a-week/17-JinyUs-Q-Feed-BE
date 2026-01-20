package com.ktb.auth.security.filter;

import com.ktb.auth.security.abstraction.AuthenticationContextManager;
import com.ktb.auth.security.abstraction.AuthenticationService;
import com.ktb.auth.security.abstraction.RequestContext;
import com.ktb.auth.security.abstraction.TokenExtractor;
import com.ktb.auth.security.adapter.HttpServletRequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터 (리팩토링)
 * - DIP 적용: 추상화에만 의존
 * - OCP 적용: 확장 가능한 구조
 * - SRP 적용: 필터 체인 조정만 담당
 *
 * 비즈니스 로직은 JwtAuthenticationService로 분리
 * Spring Security 종속 코드는 SpringSecurityContextManager로 격리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // DI: 모두 추상화(인터페이스)에 의존
    private final TokenExtractor tokenExtractor;
    private final AuthenticationService authenticationService;
    private final AuthenticationContextManager contextManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Request를 추상화로 wrapping
            RequestContext requestContext = new HttpServletRequestContext(request);

            // 2. Token 추출 (위임)
            // 3. 인증 처리 (위임)
            // 4. Context 설정 (위임)
            tokenExtractor.extractToken(requestContext)
                    .flatMap(authenticationService::authenticate)
                    .ifPresent(user -> contextManager.setAuthentication(user, requestContext));

        } catch (Exception e) {
            log.warn("JWT 인증 처리 중 예외 발생: {}", e.getMessage());
            contextManager.clearAuthentication();
        }

        // 5. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
