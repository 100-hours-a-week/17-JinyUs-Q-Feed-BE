package com.ktb.auth.security.adapter;

import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class SecurityUserAccount extends User {

    private final Long accountId;

    public SecurityUserAccount(Long accountId, List<String> roles) {
        super(
                String.valueOf(accountId),
                "",
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
        this.accountId = accountId;
    }
}
