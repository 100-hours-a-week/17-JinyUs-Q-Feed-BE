package com.ktb.auth.security.filter;

import com.ktb.auth.security.adapter.BearerTokenExtractor;
import com.ktb.auth.security.adapter.SpringSecurityContextManager;
import com.ktb.auth.security.exception.AuthFailureException;
import com.ktb.auth.security.service.JwtAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final BearerTokenExtractor tokenExtractor;
    private final JwtAuthenticationService authenticationService;
    private final SpringSecurityContextManager contextManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            tokenExtractor.extractToken(request)
                .flatMap(authenticationService::authenticate)
                .ifPresent(claims -> contextManager.setAuthentication(claims, request));

        } catch (AuthFailureException e) {
            log.warn("JWT 인증 처리 중 예외 발생: {}", e.getMessage());
            contextManager.clearAuthentication();
            authenticationEntryPoint.commence(request, response, e);

            return;
        } catch (Exception e) {
            log.warn("JWT 인증 처리 중 예외 발생: {}", e.getMessage(), e);
            contextManager.clearAuthentication();
        }

        filterChain.doFilter(request, response);
    }
}
