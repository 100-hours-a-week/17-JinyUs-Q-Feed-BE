package com.ktb.auth.service.impl;

import com.ktb.auth.client.KakaoOAuth2Client;
import com.ktb.auth.domain.OAuthProvider;
import com.ktb.auth.domain.RefreshToken;
import com.ktb.auth.domain.RevokeReason;
import com.ktb.auth.domain.TokenFamily;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.dto.AuthorizationUrlResult;
import com.ktb.auth.dto.response.KakaoUserInfoResponse;
import com.ktb.auth.dto.OAuthLoginResult;
import com.ktb.auth.dto.TokenRefreshResult;
import com.ktb.auth.dto.UserInfo;
import com.ktb.auth.exception.family.FamilyOwnershipException;
import com.ktb.auth.exception.family.TokenFamilyNotFoundException;
import com.ktb.auth.exception.oauth.OAuthProviderException;
import com.ktb.auth.exception.oauth.UnsupportedProviderException;
import com.ktb.auth.jwt.JwtProvider;
import com.ktb.auth.repository.RefreshTokenRepository;
import com.ktb.auth.repository.TokenFamilyRepository;
import com.ktb.auth.service.OAuthApplicationService;
import com.ktb.auth.service.OAuthDomainService;
import com.ktb.auth.service.RTRService;
import com.ktb.auth.service.TokenService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth Application Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuthApplicationServiceImpl implements OAuthApplicationService {

    private static final int ACCESS_TOKEN_EXPIRES_SECONDS = 600;
    private static final int REFRESH_TOKEN_EXPIRES_DAYS = 14;
    private static final String SUPPORTED_PROVIDER_KAKAO = "kakao";
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final List<String> DEFAULT_ROLES = List.of(DEFAULT_ROLE);

    private final KakaoOAuth2Client kakaoOAuth2Client;
    private final OAuthDomainService oauthDomainService;
    private final TokenService tokenService;
    private final RTRService rtrService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenFamilyRepository tokenFamilyRepository;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthorizationUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.scope}")
    private List<String> kakaoScopes;

    @Override
    public AuthorizationUrlResult getAuthorizationUrl(String provider) {
        if (!SUPPORTED_PROVIDER_KAKAO.equalsIgnoreCase(provider)) {
            throw new UnsupportedProviderException(provider);
        }

        // State 생성
        String state = oauthDomainService.generateAndStoreState(provider);

        // Scope 문자열 생성
        String scopeParam = String.join(",", kakaoScopes);

        // Kakao 인증 URL 생성
        String redirectUrl = String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&state=%s&scope=%s",
                kakaoAuthorizationUri,
                kakaoClientId,
                kakaoRedirectUri,
                state,
                scopeParam
        );

        return new AuthorizationUrlResult(redirectUrl);
    }

    @Override
    @Transactional
    public OAuthLoginResult handleCallback(String provider, String code, String state, String deviceInfo, String clientIp) {
        if (!"kakao".equalsIgnoreCase(provider)) {
            throw new UnsupportedProviderException(provider);
        }

        // 1. State 검증
        oauthDomainService.validateAndConsumeState(state);

        try {
            // 2. Authorization Code → Access Token
            String kakaoAccessToken = kakaoOAuth2Client.getAccessToken(code);

            // 3. Access Token → 사용자 정보
            KakaoUserInfoResponse userInfo = kakaoOAuth2Client.getUserInfo(kakaoAccessToken);

            // 4. UserAccount 조회 or 생성 (email 매핑)
            UserAccount account = oauthDomainService.findOrCreateAccount(
                    OAuthProvider.KAKAO,
                    userInfo.getProviderId(),
                    userInfo
            );

            boolean isNewUser = account.getLastLoginAt() == null;

            // 5. 로그인 시각 갱신
            account.updateLastLogin();

            // 6. TokenFamily 생성
            TokenFamily family = rtrService.createFamily(account.getId(), deviceInfo, clientIp);

            // 7. JWT 발급
            String accessToken = tokenService.issueAccessToken(account.getId(), DEFAULT_ROLES);
            String refreshToken = jwtProvider.createRefreshToken(account.getId(), family.getUuid());

            // 8. RefreshToken DB 저장
            String tokenHash = jwtProvider.generateTokenHash(refreshToken);
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .family(family)
                    .tokenHash(tokenHash)
                    .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRES_DAYS))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            log.info("OAuth 로그인 성공: accountId={}, isNewUser={}", account.getId(), isNewUser);

            return new OAuthLoginResult(
                    accessToken,
                    refreshToken,
                    new UserInfo(account.getNickname(), isNewUser)
            );

        } catch (Exception e) {
            log.error("OAuth 로그인 실패: provider={}, error={}", provider, e.getMessage(), e);
            throw new OAuthProviderException();
        }
    }

    @Override
    @Transactional
    public TokenRefreshResult refreshTokens(String refreshToken) {
        // 1. Refresh Token 검증
        TokenService.RefreshTokenClaims claims = tokenService.validateRefreshToken(refreshToken);

        // 2. Token Hash로 DB 조회
        String tokenHash = jwtProvider.generateTokenHash(refreshToken);
        TokenService.RefreshTokenEntity tokenEntity = tokenService.findByTokenHash(tokenHash);

        // 3. 재사용 감지
        rtrService.detectReuse(tokenEntity);

        // 4. Family 활성 상태 확인
        rtrService.validateFamilyActive(tokenEntity.familyId());

        // 5. 기존 토큰 사용 처리
        rtrService.markAsUsed(tokenEntity.id());

        // 6. 새로운 Access Token 발급
        String newAccessToken = tokenService.issueAccessToken(claims.userId(), DEFAULT_ROLES);

        // 7. 새로운 Refresh Token 발급 (RTR)
        String newRefreshToken = jwtProvider.createRefreshToken(claims.userId(), claims.familyUuid());

        // 8. 새로운 RefreshToken DB 저장
        TokenFamily family = tokenFamilyRepository.findByUuid(claims.familyUuid())
                .orElseThrow(() -> new TokenFamilyNotFoundException(claims.familyUuid()));

        String newTokenHash = jwtProvider.generateTokenHash(newRefreshToken);
        RefreshToken newTokenEntity = RefreshToken.builder()
                .family(family)
                .tokenHash(newTokenHash)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRES_DAYS))
                .build();
        refreshTokenRepository.save(newTokenEntity);

        family.updateLastUsed();

        log.info("Token Refresh 성공: userId={}, familyUuid={}", claims.userId(), claims.familyUuid());

        return new TokenRefreshResult(newAccessToken, newRefreshToken, ACCESS_TOKEN_EXPIRES_SECONDS);
    }

    @Override
    @Transactional
    public void logout(Long accountId, String refreshToken) {
        // Refresh Token 검증
        TokenService.RefreshTokenClaims claims = tokenService.validateRefreshToken(refreshToken);

        if (!claims.userId().equals(accountId)) {
            throw new FamilyOwnershipException();
        }

        // Family UUID로 Family 조회 및 무효화
        TokenFamily family = tokenFamilyRepository.findByUuid(claims.familyUuid())
                .orElseThrow(() -> new TokenFamilyNotFoundException(claims.familyUuid()));

        family.revoke(RevokeReason.USER_LOGOUT);

        log.info("로그아웃 성공: accountId={}, familyUuid={}", accountId, claims.familyUuid());
    }

    @Override
    @Transactional
    public int logoutAll(Long accountId) {
        int count = tokenFamilyRepository.revokeAllByAccountId(accountId, RevokeReason.USER_LOGOUT);

        log.info("전체 로그아웃 성공: accountId={}, revokedCount={}", accountId, count);
        return count;
    }

}
