package com.ktb.auth.integration;

import com.ktb.auth.client.KakaoOAuth2Client;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.dto.KakaoAccount;
import com.ktb.auth.dto.KakaoProfile;
import com.ktb.auth.dto.KakaoUserInfo;
import com.ktb.auth.repository.RefreshTokenRepository;
import com.ktb.auth.repository.TokenFamilyRepository;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.auth.repository.UserOAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("OAuth 통합 테스트")
class OAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserOAuthRepository userOAuthRepository;

    @Autowired
    private TokenFamilyRepository tokenFamilyRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private KakaoOAuth2Client kakaoOAuth2Client;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        tokenFamilyRepository.deleteAll();
        userOAuthRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 OAuth 로그인 플로우 - 신규 사용자")
    void fullOAuthLoginFlow_WithNewUser() throws Exception {
        // given
        String state = "generated-state";
        String code = "auth-code-123";
        String kakaoAccessToken = "kakao.access.token";
        KakaoUserInfo userInfo = new KakaoUserInfo(12345L, new KakaoAccount("newuser@example.com", new KakaoProfile("신규유저", null)));

        when(kakaoOAuth2Client.getAccessToken(code)).thenReturn(kakaoAccessToken);
        when(kakaoOAuth2Client.getUserInfo(kakaoAccessToken)).thenReturn(userInfo);

        // 1. Authorization URL 생성
        MvcResult urlResult = mockMvc.perform(get("/api/auth/oauth/authorization-url")
                        .param("provider", "kakao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authorizationUrl").exists())
                .andReturn();

        // 2. OAuth 콜백 처리 (신규 사용자)
        MvcResult callbackResult = mockMvc.perform(get("/api/auth/oauth/kakao/callback")
                        .param("code", code)
                        .param("state", extractStateFromUrl(urlResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.data.user.nickname").value("신규유저"))
                .andExpect(jsonPath("$.data.user.isNewUser").value(true))
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        // 3. Refresh Token 쿠키 확인
        Cookie refreshTokenCookie = callbackResult.getResponse().getCookie("refreshToken");
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.getSecure()).isTrue();

        // 4. DB 확인
        assertThat(userAccountRepository.count()).isEqualTo(1);
        assertThat(userOAuthRepository.count()).isEqualTo(1);
        assertThat(tokenFamilyRepository.count()).isEqualTo(1);
        assertThat(refreshTokenRepository.count()).isEqualTo(1);

        UserAccount savedUser = userAccountRepository.findAll().get(0);
        assertThat(savedUser.getNickname()).isEqualTo("신규유저");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    @DisplayName("RTR (Refresh Token Rotation) 플로우")
    void refreshTokenRotationFlow() throws Exception {
        // given
        setupExistingUser();
        String state = generateStateAndLogin();
        String oldRefreshToken = extractRefreshTokenFromCookie(performLogin(state));

        // 1. Refresh Token 사용
        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", oldRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        String newRefreshToken = extractRefreshTokenFromCookie(refreshResult);
        assertThat(newRefreshToken).isNotEqualTo(oldRefreshToken);

        // 2. 이전 Refresh Token 재사용 시도 → 예외 발생
        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", oldRefreshToken)))
                .andExpect(status().isForbidden()); // TOKEN_REUSE_DETECTED

        // 3. 새 Refresh Token도 사용 불가 확인 (Family 폐기됨)
        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", newRefreshToken)))
                .andExpect(status().isUnauthorized()); // FAMILY_REVOKED
    }

    @Test
    @DisplayName("다중 기기 세션 관리")
    void multiDeviceSessionManagement() throws Exception {
        // given
        setupExistingUser();

        // 1. 2개 기기에서 로그인
        String state1 = generateStateAndLogin();
        String device1RefreshToken = extractRefreshTokenFromCookie(performLogin(state1));

        String state2 = generateStateAndLogin();
        String device2RefreshToken = extractRefreshTokenFromCookie(performLogin(state2));

        // 2. 각 기기에서 Token Refresh
        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", device1RefreshToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", device2RefreshToken)))
                .andExpect(status().isOk());

        assertThat(tokenFamilyRepository.count()).isEqualTo(2);

        // 3. 전체 로그아웃
        mockMvc.perform(post("/api/auth/logout/all").with(csrf())
                        .header("Authorization", "Bearer valid.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.revokedSessionsCount").value(2));

        // 4. 모든 세션 무효화 확인
        assertThat(tokenFamilyRepository.findAll())
                .allMatch(family -> family.isRevoked());
    }

    @Test
    @DisplayName("보안 시나리오 - Refresh Token 탈취 후 재사용 시도")
    void securityScenario_RefreshTokenTheftAndReuse() throws Exception {
        // given
        setupExistingUser();
        String state = generateStateAndLogin();
        String stolenRefreshToken = extractRefreshTokenFromCookie(performLogin(state));

        // 1. 정상 사용자가 Token Refresh
        MvcResult legitimateRefresh = mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", stolenRefreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        String newRefreshToken = extractRefreshTokenFromCookie(legitimateRefresh);

        // 2. 공격자가 탈취한 토큰 재사용 시도 → Family 폐기
        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", stolenRefreshToken)))
                .andExpect(status().isForbidden());

        // 3. 정상 사용자의 새 토큰도 무효화됨 (보안을 위한 Family 폐기)
        mockMvc.perform(post("/api/auth/tokens").with(csrf())
                        .cookie(new Cookie("refreshToken", newRefreshToken)))
                .andExpect(status().isUnauthorized());

        // 4. Family가 폐기되었는지 확인
        assertThat(tokenFamilyRepository.findAll())
                .allMatch(family -> family.isRevoked());
    }

    // Helper methods
    private void setupExistingUser() {
        UserAccount user = UserAccount.builder()
                .email("existing@example.com")
                .nickname("기존유저")
                .build();
        userAccountRepository.save(user);
    }

    private String generateStateAndLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/oauth/authorization-url")
                        .param("provider", "kakao"))
                .andReturn();
        return extractStateFromUrl(result);
    }

    private MvcResult performLogin(String state) throws Exception {
        String code = "auth-code";
        KakaoUserInfo userInfo = new KakaoUserInfo(12345L, new KakaoAccount("user@example.com", new KakaoProfile("유저", null)));

        when(kakaoOAuth2Client.getAccessToken(anyString())).thenReturn("kakao.token");
        when(kakaoOAuth2Client.getUserInfo(anyString())).thenReturn(userInfo);

        return mockMvc.perform(get("/api/auth/oauth/kakao/callback")
                        .param("code", code)
                        .param("state", state))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String extractStateFromUrl(MvcResult result) {
        // URL에서 state 파라미터 추출 (실제 구현 필요)
        return "extracted-state";
    }

    private String extractRefreshTokenFromCookie(MvcResult result) {
        Cookie cookie = result.getResponse().getCookie("refreshToken");
        return cookie != null ? cookie.getValue() : null;
    }
}
