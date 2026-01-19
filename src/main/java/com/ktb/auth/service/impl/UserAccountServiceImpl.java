package com.ktb.auth.service.impl;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.service.UserAccountService;
import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount findById(Long accountId) {
        return userAccountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public UserAccount createAccount(String email, String nickname) {
        UserAccount account = UserAccount.createEmailAccount(email, nickname);
        return userAccountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateLastLoginAt(Long accountId) {
        UserAccount account = findById(accountId);
        account.updateLastLogin();
    }

    @Override
    @Transactional
    public void deactivateAccount(Long accountId) {
        UserAccount account = findById(accountId);
        account.withdraw();
    }

    /**
     * 계정 미발견 예외
     */
    private static class AccountNotFoundException extends BusinessException {
        public AccountNotFoundException(Long accountId) {
            super(ErrorCode.ACCOUNT_NOT_FOUND,
                    String.format("계정을 찾을 수 없습니다. accountId=%d", accountId));
        }
    }
}
