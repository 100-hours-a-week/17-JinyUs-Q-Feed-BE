package com.ktb.auth.service.impl;

import com.ktb.auth.jwt.JwtProperties;
import com.ktb.auth.jwt.JwtProvider;
import com.ktb.auth.repository.RefreshTokenRepository;
import com.ktb.auth.service.TokenService;
import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String issueAccessToken(Long accountId, List<String> roles) {
        return jwtProvider.createAccessToken(accountId, roles);
    }

    @Override
    @Transactional
    public String issueRefreshToken(Long accountId, Long familyId) {
        // TODO
        // familyId를 사용하여 실제 Family 조회가 필요하지만 우선 간단하게 처리
        // 실제로는 TokenFamilyRepository에서 조회해야 함
        throw new UnsupportedOperationException("issueRefreshToken은 RTRService에서 처리합니다");
    }

    @Override
    public TokenService.TokenClaims validateAccessToken(String accessToken) {
        try {
            Claims claims = jwtProvider.validateAccessToken(accessToken);
            Long userId = claims.get("userId", Long.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            return new TokenService.TokenClaims(userId, roles);
        } catch (Exception e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }

    @Override
    public TokenService.RefreshTokenClaims validateRefreshToken(String refreshToken) {
        try {
            Claims claims = jwtProvider.validateRefreshToken(refreshToken);
            Long userId = claims.get("userId", Long.class);
            String familyUuid = claims.get("familyUuid", String.class);

            return new TokenService.RefreshTokenClaims(userId, familyUuid);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException(e.getMessage());
        }
    }

    @Override
    public TokenService.RefreshTokenEntity findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHashWithFamily(tokenHash)
                .map(token -> new TokenService.RefreshTokenEntity(
                        token.getId(),
                        token.getFamily().getId(),
                        token.getUsed(),
                        token.getExpiresAt()
                ))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh Token을 찾을 수 없습니다."));
    }

    // 예외 클래스들
    private static class InvalidAccessTokenException extends BusinessException {
        public InvalidAccessTokenException(String message) {
            super(ErrorCode.INVALID_ACCESS_TOKEN, message);
        }
    }

    private static class InvalidRefreshTokenException extends BusinessException {
        public InvalidRefreshTokenException(String message) {
            super(ErrorCode.INVALID_REFRESH_TOKEN, message);
        }
    }
}
