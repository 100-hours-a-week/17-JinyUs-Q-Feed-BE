package com.ktb.auth.service.impl;

import com.ktb.auth.domain.RefreshToken;
import com.ktb.auth.domain.RevokeReason;
import com.ktb.auth.domain.TokenFamily;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.repository.RefreshTokenRepository;
import com.ktb.auth.repository.TokenFamilyRepository;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.service.RTRService;
import com.ktb.auth.service.TokenService;
import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RTR Service 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RTRServiceImpl implements RTRService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenFamilyRepository tokenFamilyRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public void detectReuse(TokenService.RefreshTokenEntity tokenEntity) {
        if (tokenEntity.used()) {
            // 토큰 재사용 감지 -> Family 무효화
            revokeFamily(tokenEntity.familyId(), RevokeReason.REUSE_DETECTED);
            throw new TokenReuseDetectedException();
        }
    }

    @Override
    @Transactional
    public void revokeFamily(Long familyId, RevokeReason reason) {
        TokenFamily family = tokenFamilyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("TokenFamily를 찾을 수 없습니다."));

        family.revoke(reason);
    }

    @Override
    @Transactional
    public void markAsUsed(Long refreshTokenId) {
        RefreshToken token = refreshTokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken을 찾을 수 없습니다."));

        token.markAsUsed();
    }

    @Override
    @Transactional
    public TokenFamily createFamily(Long accountId, String deviceInfo, String clientIp) {
        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TokenFamily family = TokenFamily.create(account, deviceInfo, clientIp);
        return tokenFamilyRepository.save(family);
    }

    @Override
    public void validateFamilyActive(Long familyId) {
        TokenFamily family = tokenFamilyRepository.findById(familyId)
                .orElseThrow(FamilyRevokedException::new);

        if (family.isRevoked()) {
            throw new FamilyRevokedException();
        }

        if (family.isExpired()) {
            throw new FamilyRevokedException();
        }
    }

    // 예외 클래스
    private static class TokenReuseDetectedException extends BusinessException {
        public TokenReuseDetectedException() {
            super(ErrorCode.TOKEN_REUSE_DETECTED);
        }
    }

    private static class FamilyRevokedException extends BusinessException {
        public FamilyRevokedException() {
            super(ErrorCode.FAMILY_REVOKED);
        }
    }
}
