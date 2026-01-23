package com.ktb.swagger.auth;

import com.ktb.auth.dto.AuthorizationUrlResult;
import com.ktb.auth.dto.LogoutAllResponse;
import com.ktb.auth.dto.OAuthLoginResponseDto;
import com.ktb.auth.dto.TokenRefreshResponseDto;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import com.ktb.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "OAuth API", description = "OAuth 인증 API")
public interface OAuthApi {

    @Operation(summary = "OAuth 로그인 시작", description = "OAuth 인증 URL을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "지원하지 않는 provider",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<AuthorizationUrlResult>> getAuthorizationUrl(
            @Parameter(description = "OAuth 제공자", example = "kakao") String provider
    );

    @Operation(summary = "OAuth 콜백", description = "OAuth 콜백을 처리하고 토큰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<OAuthLoginResponseDto>> handleCallback(
            @Parameter(description = "OAuth 제공자", example = "kakao") String provider,
            @Parameter(description = "Authorization code") String code,
            @Parameter(description = "CSRF state") String state,
            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 Access Token을 재발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "갱신 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Refresh Token 누락",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<TokenRefreshResponseDto>> refreshTokens(
            @Parameter(hidden = true) String refreshToken,
            HttpServletResponse response
    );

    @Operation(summary = "로그아웃", description = "단일 기기 로그아웃을 수행합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) SecurityUserAccount principal,
            @Parameter(hidden = true) String refreshToken,
            HttpServletResponse response
    );

    @Operation(summary = "전체 로그아웃", description = "모든 기기 로그아웃을 수행합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<LogoutAllResponse>> logoutAll(
            @Parameter(hidden = true) SecurityUserAccount principal,
            HttpServletResponse response
    );
}
