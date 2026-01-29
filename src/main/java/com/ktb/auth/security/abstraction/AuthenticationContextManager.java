package com.ktb.auth.security.abstraction;

public interface AuthenticationContextManager {

    void setAuthentication(AuthenticatedUser user, RequestContext request);

    void clearAuthentication();
}
