package com.ktb.auth.security.abstraction;

import java.util.List;

public interface AuthenticatedUser {

    Long getUserId();

    String getEmail();

    List<String> roles();

    boolean isActive();
}
