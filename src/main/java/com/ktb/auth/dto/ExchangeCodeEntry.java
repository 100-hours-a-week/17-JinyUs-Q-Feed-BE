package com.ktb.auth.dto;

public record ExchangeCodeEntry(
    OAuthExchangePayload payload,
    long createdAt
) {
}
