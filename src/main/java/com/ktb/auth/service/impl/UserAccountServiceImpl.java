package com.ktb.auth.service.impl;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.exception.account.AccountNotFoundException;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.service.UserAccountService;
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

}
