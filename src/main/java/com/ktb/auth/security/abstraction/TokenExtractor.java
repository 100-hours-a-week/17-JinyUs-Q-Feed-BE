package com.ktb.auth.security.abstraction;

import java.util.Optional;

public interface TokenExtractor {

    Optional<String> extractToken(RequestContext request);
}
