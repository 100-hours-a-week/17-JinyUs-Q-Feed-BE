package com.ktb.auth.security.adapter;

import com.ktb.auth.domain.UserAccount;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class SecurityUserAccount extends User {

    private final UserAccount account;

    public SecurityUserAccount(UserAccount account) {
        super(
                account.getEmail(),
                "",  // OAuth 전용이므로 password 없음
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        this.account = account;
    }
}
