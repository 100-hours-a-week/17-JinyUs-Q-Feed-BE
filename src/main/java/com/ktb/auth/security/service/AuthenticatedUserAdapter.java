package com.ktb.auth.security.service;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.security.abstraction.AuthenticatedUser;

import java.util.List;

public record AuthenticatedUserAdapter(UserAccount account, List<String> roles) implements AuthenticatedUser {

    @Override
    public Long getUserId() {
        return account.getId();
    }

    @Override
    public String getEmail() {
        return account.getEmail();
    }

    @Override
    public boolean isActive() {
        return account.isActive();
    }

}
