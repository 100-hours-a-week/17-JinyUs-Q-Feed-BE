package com.ktb.auth.controller;

import com.ktb.auth.dto.AuthorizationUrlResult;
import com.ktb.auth.dto.LogoutAllResponse;
import com.ktb.auth.dto.OAuthLoginResponseDto;
import com.ktb.auth.dto.OAuthLoginResult;
import com.ktb.auth.dto.TokenRefreshResponseDto;
import com.ktb.auth.dto.TokenRefreshResult;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import com.ktb.auth.service.CookieService;
import com.ktb.auth.service.OAuthApplicationService;
import com.ktb.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth 인증 Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthApplicationService oauthApplicationService;
    private final CookieService cookieService;

    /**
     * OAuth 인증 URL 생성
     * GET /api/auth/oauth/authorization-url?provider=kakao
     */
    @GetMapping("/oauth/authorization-url")
    public ResponseEntity<ApiResponse<AuthorizationUrlResult>> getAuthorizationUrl(
            @RequestParam String provider
    ) {
        AuthorizationUrlResult result = oauthApplicationService.getAuthorizationUrl(provider);

        return ResponseEntity.ok(
                new ApiResponse<>("redirect_to_oauth_provider", result)
        );
    }

    /**
     * OAuth 콜백 처리
     * GET /api/auth/oauth/{provider}/callback?code=xxx&state=yyy
     */
    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<ApiResponse<OAuthLoginResponseDto>> handleCallback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam String state,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String deviceInfo = extractDeviceInfo(request);
        String clientIp = extractClientIp(request);

        OAuthLoginResult result = oauthApplicationService.handleCallback(
                provider, code, state, deviceInfo, clientIp
        );

        // Access Token을 Response Header로 설정
        response.setHeader("Authorization", "Bearer " + result.accessToken());

        // Refresh Token을 HTTP-only 쿠키로 설정
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(result.refreshToken());
        response.addCookie(refreshTokenCookie);

        // 응답에는 User 정보만 포함 (토큰 정보 제외)
        OAuthLoginResponseDto responseDto = new OAuthLoginResponseDto(result.user());

        return ResponseEntity.ok(
                new ApiResponse<OAuthLoginResponseDto>("oauth_login_success", responseDto)
        );
    }

    /**
     * Token Refresh
     * POST /api/auth/tokens
     */
    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<TokenRefreshResponseDto>> refreshTokens(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("missing_refresh_token", null));
        }

        TokenRefreshResult result = oauthApplicationService.refreshTokens(refreshToken);

        // Access Token을 Response Header로 설정
        response.setHeader("Authorization", "Bearer " + result.accessToken());

        // 새로운 Refresh Token을 HTTP-only 쿠키로 설정
        Cookie newRefreshTokenCookie = cookieService.createRefreshTokenCookie(result.refreshToken());
        response.addCookie(newRefreshTokenCookie);

        // 응답에는 expiresIn만 포함 (토큰 정보 제외)
        TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto(result.expiresIn());

        return ResponseEntity.ok(
                new ApiResponse<TokenRefreshResponseDto>("token_refresh_success", responseDto)
        );
    }

    /**
     * 로그아웃 (단일 기기)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal SecurityUserAccount principal,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>("unauthorized_request", null));
        }

        if (refreshToken != null) {
            oauthApplicationService.logout(principal.getAccount().getId(), refreshToken);
        }

        // Refresh Token 쿠키 제거
        Cookie expiredCookie = cookieService.createExpiredRefreshTokenCookie();
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(
                new ApiResponse<>("logout_success", null)
        );
    }

    /**
     * 전체 기기 로그아웃
     * POST /api/auth/logout/all
     */
    @PostMapping("/logout/all")
    public ResponseEntity<ApiResponse<LogoutAllResponse>> logoutAll(
            @AuthenticationPrincipal SecurityUserAccount principal,
            HttpServletResponse response
    ) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>("unauthorized_request", null));
        }

        int revokedCount = oauthApplicationService.logoutAll(principal.getAccount().getId());

        // Refresh Token 쿠키 제거
        Cookie expiredCookie = cookieService.createExpiredRefreshTokenCookie();
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(
                new ApiResponse<>("all_sessions_logged_out", new LogoutAllResponse(revokedCount))
        );
    }

    /**
     * 디바이스 정보 추출
     */
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown Device";
    }

    /**
     * 클라이언트 IP 추출
     */
    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
