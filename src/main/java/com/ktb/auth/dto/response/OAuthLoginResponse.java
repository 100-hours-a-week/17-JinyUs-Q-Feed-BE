package com.ktb.auth.dto.response;

import com.ktb.auth.dto.UserInfo;

public record OAuthLoginResponse(UserInfo user) {
}
