package com.ktb.auth.dto;

public record OAuthExchangePayload(
        Long accountId,
        String nickname,
        boolean isNewUser,
        String deviceInfo,
        String clientIp
) {
}
