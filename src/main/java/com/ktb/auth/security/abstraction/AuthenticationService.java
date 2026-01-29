package com.ktb.auth.security.abstraction;

import java.util.Optional;

public interface AuthenticationService {

    Optional<AuthenticatedUser> authenticate(String token);
}
