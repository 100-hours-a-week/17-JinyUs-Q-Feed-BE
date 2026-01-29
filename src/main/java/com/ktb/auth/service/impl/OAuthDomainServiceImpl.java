package com.ktb.auth.service.impl;

import com.ktb.auth.domain.OAuthProvider;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.domain.UserOAuth;
import com.ktb.auth.dto.ExchangeCodeEntry;
import com.ktb.auth.dto.OAuthExchangePayload;
import com.ktb.auth.exception.oauth.InvalidStateException;
import com.ktb.auth.exception.oauth.OAuthConnectionNotFoundException;
import com.ktb.auth.exception.oauth.InvalidExchangeCodeException;
import com.ktb.auth.dto.response.KakaoUserInfoResponse;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.repository.UserOAuthRepository;
import com.ktb.auth.service.OAuthDomainService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final Map<String, ExchangeCodeEntry> exchangeCodeStore = new ConcurrentHashMap<>();
    private static final long EXCHANGE_CODE_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(1);

    private static final String DEFAULT_NICKNAME_PREFIX = "사용자";
    private static final int DEFAULT_NICKNAME_SUFFIX_LENGTH = 6;

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
            throw new InvalidStateException();
        }

        long elapsed = System.currentTimeMillis() - timestamp;
        if (elapsed > STATE_EXPIRATION_MS) {
            throw new InvalidStateException();
        }
    }

    @Override
    public String generateAndStoreExchangeCode(OAuthExchangePayload payload) {
        String exchangeCode = UUID.randomUUID().toString();
        exchangeCodeStore.put(exchangeCode, new ExchangeCodeEntry(payload, System.currentTimeMillis()));

        log.info("Exchange code 생성: accountId={}, code={}", payload.accountId(), exchangeCode);
        return exchangeCode;
    }

    @Override
    public OAuthExchangePayload consumeExchangeCode(String exchangeCode) {
        ExchangeCodeEntry entry = exchangeCodeStore.remove(exchangeCode);
        if (entry == null) {
            throw new InvalidExchangeCodeException();
        }

        long elapsed = System.currentTimeMillis() - entry.createdAt();
        if (elapsed > EXCHANGE_CODE_EXPIRATION_MS) {
            throw new InvalidExchangeCodeException();
        }

        return entry.payload();
    }

    @Override
    @Transactional
    public UserAccount findOrCreateAccount(OAuthProvider provider, String providerUserId, KakaoUserInfoResponse userInfo) {
        // 기존 OAuth 연동 조회
        return userOAuthRepository.findByProviderAndProviderUserIdWithAccount(provider, providerUserId)
                .map(UserOAuth::getAccount)
                .orElseGet(() -> createNewAccount(provider, providerUserId, userInfo));
    }

    @Override
    @Transactional
    public void updateOAuthLoginInfo(Long oauthId) {
        UserOAuth userOAuth = userOAuthRepository.findById(oauthId)
                .orElseThrow(() -> new OAuthConnectionNotFoundException(oauthId));

        userOAuth.updateLastLogin();
    }

    /**
     * 신규 계정 생성 (OAuth 최초 로그인)
     */
    private UserAccount createNewAccount(OAuthProvider provider, String providerUserId, KakaoUserInfoResponse userInfo) {
        log.info("신규 계정 생성: provider={}, email={}", provider, userInfo.getEmail());

        String nickname = userInfo.getNickname() != null
                ? userInfo.getNickname()
                : DEFAULT_NICKNAME_PREFIX + providerUserId.substring(
                        0,
                        Math.min(DEFAULT_NICKNAME_SUFFIX_LENGTH, providerUserId.length())
                );

        UserAccount account = UserAccount.createEmailAccount(userInfo.getEmail(), nickname);
        userAccountRepository.save(account);

        UserOAuth accountOAuthInfo = UserOAuth.create(account, provider, providerUserId);
        userOAuthRepository.save(accountOAuthInfo);

        return account;
    }
}
