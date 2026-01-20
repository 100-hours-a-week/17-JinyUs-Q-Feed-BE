package com.ktb.auth.service.impl;

import com.ktb.auth.domain.OAuthProvider;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.domain.UserOAuth;
import com.ktb.auth.dto.KakaoUserInfo;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.repository.UserOAuthRepository;
import com.ktb.auth.service.OAuthDomainService;
import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth Domain Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuthDomainServiceImpl implements OAuthDomainService {

    private final UserOAuthRepository userOAuthRepository;
    private final UserAccountRepository userAccountRepository;

    // 임시 State 저장소 (실제로는 Redis 사용 권장)
    private final Map<String, Long> stateStore = new ConcurrentHashMap<>();
    private static final long STATE_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(5);

    @Override
    public String generateAndStoreState(String provider) {
        String state = UUID.randomUUID().toString();
        stateStore.put(state, System.currentTimeMillis());

        log.info("State 생성: provider={}, state={}", provider, state);
        return state;
    }

    @Override
    public void validateAndConsumeState(String state) {
        Long timestamp = stateStore.remove(state);

        if (timestamp == null) {
            throw new InvalidStateException("State가 존재하지 않거나 이미 사용되었습니다.");
        }

        long elapsed = System.currentTimeMillis() - timestamp;
        if (elapsed > STATE_EXPIRATION_MS) {
            throw new InvalidStateException("State가 만료되었습니다.");
        }
    }

    @Override
    @Transactional
    public UserAccount findOrCreateAccount(OAuthProvider provider, String providerUserId, KakaoUserInfo userInfo) {
        // 기존 OAuth 연동 조회
        return userOAuthRepository.findByProviderAndProviderUserIdWithAccount(provider, providerUserId)
                .map(UserOAuth::getAccount)
                .orElseGet(() -> createNewAccount(provider, providerUserId, userInfo));
    }

    @Override
    @Transactional
    public void updateOAuthLoginInfo(Long oauthId) {
        UserOAuth userOAuth = userOAuthRepository.findById(oauthId)
                .orElseThrow(() -> new IllegalArgumentException("OAuth 연동을 찾을 수 없습니다."));

        userOAuth.updateLastLogin();
    }

    /**
     * 신규 계정 생성 (OAuth 최초 로그인)
     */
    private UserAccount createNewAccount(OAuthProvider provider, String providerUserId, KakaoUserInfo userInfo) {
        log.info("신규 계정 생성: provider={}, email={}", provider, userInfo.getEmail());

        // UserAccount 생성 (email 매핑)
        String nickname = userInfo.getNickname() != null
                ? userInfo.getNickname()
                : "사용자" + providerUserId.substring(0, Math.min(6, providerUserId.length()));

        UserAccount account = UserAccount.createEmailAccount(userInfo.getEmail(), nickname);
        userAccountRepository.save(account);

        // UserOAuth 연동 생성
        UserOAuth.create(account, provider, providerUserId);

        return account;
    }

    // 예외 클래스
    private static class InvalidStateException extends BusinessException {
        public InvalidStateException(String message) {
            super(ErrorCode.INVALID_STATE, message);
        }
    }
}
