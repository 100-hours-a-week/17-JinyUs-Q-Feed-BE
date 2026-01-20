package com.ktb.auth.security.adapter;

import com.ktb.auth.security.abstraction.RequestContext;
import com.ktb.auth.security.abstraction.TokenExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Bearer 토큰 추출 구현
 * - Authorization: Bearer {token} 형식에서 토큰 추출
 */
@Component
public class BearerTokenExtractor implements TokenExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Optional<String> extractToken(RequestContext request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
        }

        return Optional.empty();
    }
}
