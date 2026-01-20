package com.ktb.auth.controller;

import com.ktb.auth.dto.AuthorizationUrlResult;
import com.ktb.auth.dto.OAuthLoginResult;
import com.ktb.auth.dto.TokenRefreshResult;
import com.ktb.auth.dto.UserInfo;
import com.ktb.auth.service.CookieService;
import com.ktb.auth.service.OAuthApplicationService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthController.class)
@DisplayName("OAuthController 단위 테스트")
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OAuthApplicationService oauthApplicationService;

    @MockitoBean
    private CookieService cookieService;

    private static final String ACCESS_TOKEN = "access.token.jwt";
    private static final String REFRESH_TOKEN = "refresh.token.jwt";
    private static final String NEW_REFRESH_TOKEN = "new.refresh.token";

    @Test
    @DisplayName("Authorization URL 요청 성공")
    void getAuthorizationUrl_ShouldSucceed() throws Exception {
        // given
        AuthorizationUrlResult result = new AuthorizationUrlResult("https://kauth.kakao.com/oauth/authorize?...");
        when(oauthApplicationService.getAuthorizationUrl("kakao")).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/auth/oauth/authorization-url")
                        .param("provider", "kakao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authorizationUrl").value("https://kauth.kakao.com/oauth/authorize?..."));

        verify(oauthApplicationService).getAuthorizationUrl("kakao");
    }

    @Test
    @DisplayName("OAuth 콜백 처리 성공 + Refresh Token 쿠키 설정 확인")
    void handleCallback_ShouldSucceedAndSetCookie() throws Exception {
        // given
        UserInfo userInfo = new UserInfo("테스트유저", true);
        OAuthLoginResult result = new OAuthLoginResult(ACCESS_TOKEN, REFRESH_TOKEN, userInfo);
        Cookie mockCookie = new Cookie("refreshToken", REFRESH_TOKEN);

        when(oauthApplicationService.handleCallback(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(result);
        when(cookieService.createRefreshTokenCookie(REFRESH_TOKEN)).thenReturn(mockCookie);

        // when & then
        mockMvc.perform(get("/api/v1/auth/oauth/kakao/callback")
                        .param("code", "auth-code")
                        .param("state", "state-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist()) // 응답에 포함되지 않아야 함
                .andExpect(jsonPath("$.data.user.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.user.isNewUser").value(true))
                .andExpect(cookie().exists("refreshToken"));

        verify(cookieService).createRefreshTokenCookie(REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Token Refresh 성공 + 새 Refresh Token 쿠키 설정 확인")
    void refreshTokens_ShouldSucceedAndSetNewCookie() throws Exception {
        // given
        TokenRefreshResult result = new TokenRefreshResult("new.access.token", NEW_REFRESH_TOKEN, 600);
        Cookie newCookie = new Cookie("refreshToken", NEW_REFRESH_TOKEN);

        when(oauthApplicationService.refreshTokens(REFRESH_TOKEN)).thenReturn(result);
        when(cookieService.createRefreshTokenCookie(NEW_REFRESH_TOKEN)).thenReturn(newCookie);

        // when & then
        mockMvc.perform(post("/api/v1/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new.access.token"))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist()) // 응답에 포함되지 않아야 함
                .andExpect(cookie().value("refreshToken", NEW_REFRESH_TOKEN));

        verify(oauthApplicationService).refreshTokens(REFRESH_TOKEN);
        verify(cookieService).createRefreshTokenCookie(NEW_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("쿠키 없이 Refresh 요청 시 400 에러")
    void refreshTokens_WithoutCookie_ShouldReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/auth/tokens").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("missing_refresh_token"));
    }

    @Test
    @WithMockUser
    @DisplayName("로그아웃 성공 + 쿠키 삭제 확인")
    void logout_ShouldSucceedAndDeleteCookie() throws Exception {
        // given
        Cookie expiredCookie = new Cookie("refreshToken", "");
        expiredCookie.setMaxAge(0);
        when(cookieService.createExpiredRefreshTokenCookie()).thenReturn(expiredCookie);

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout").with(csrf())
                        .cookie(new Cookie("refreshToken", REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refreshToken", 0));

        verify(cookieService).createExpiredRefreshTokenCookie();
    }

    @Test
    @WithMockUser
    @DisplayName("전체 로그아웃 성공 + 쿠키 삭제 확인")
    void logoutAll_ShouldSucceedAndDeleteCookie() throws Exception {
        // given
        when(oauthApplicationService.logoutAll(any())).thenReturn(3);
        Cookie expiredCookie = new Cookie("refreshToken", "");
        expiredCookie.setMaxAge(0);
        when(cookieService.createExpiredRefreshTokenCookie()).thenReturn(expiredCookie);

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout/all").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.revokedSessionsCount").value(3))
                .andExpect(cookie().maxAge("refreshToken", 0));

        verify(cookieService).createExpiredRefreshTokenCookie();
    }
}
