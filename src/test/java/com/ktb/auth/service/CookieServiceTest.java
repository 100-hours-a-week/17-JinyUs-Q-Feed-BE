package com.ktb.auth.service;

import com.ktb.auth.jwt.JwtProperties;
import com.ktb.auth.service.impl.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CookieService 단위 테스트")
class CookieServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private CookieServiceImpl cookieService;

    private static final String COOKIE_NAME = "refreshToken";
    private static final Long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14일 (밀리초)
    private static final String SAMPLE_REFRESH_TOKEN = "sample.refresh.token";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getRefreshTokenCookieName()).thenReturn(COOKIE_NAME);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    @DisplayName("Refresh Token 쿠키 생성 시 올바른 속성이 설정되어야 한다")
    void createRefreshTokenCookie_ShouldSetCorrectAttributes() {
        // when
        Cookie cookie = cookieService.createRefreshTokenCookie(SAMPLE_REFRESH_TOKEN);

        // then
        assertThat(cookie.getName()).isEqualTo(COOKIE_NAME);
        assertThat(cookie.getValue()).isEqualTo(SAMPLE_REFRESH_TOKEN);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/auth");
        assertThat(cookie.getMaxAge()).isEqualTo((int) (REFRESH_TOKEN_EXPIRATION / 1000));
    }

    @Test
    @DisplayName("Refresh Token 쿠키의 MaxAge는 초 단위로 변환되어야 한다")
    void createRefreshTokenCookie_ShouldConvertMaxAgeToSeconds() {
        // when
        Cookie cookie = cookieService.createRefreshTokenCookie(SAMPLE_REFRESH_TOKEN);

        // then
        // 14일 = 1,209,600,000ms = 1,209,600초
        assertThat(cookie.getMaxAge()).isEqualTo(1209600);
    }

    @Test
    @DisplayName("만료된 Refresh Token 쿠키 생성 시 빈 값과 MaxAge=0이 설정되어야 한다")
    void createExpiredRefreshTokenCookie_ShouldSetEmptyValueAndZeroMaxAge() {
        // when
        Cookie cookie = cookieService.createExpiredRefreshTokenCookie();

        // then
        assertThat(cookie.getName()).isEqualTo(COOKIE_NAME);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/auth");
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    @DisplayName("만료된 쿠키도 동일한 보안 속성을 가져야 한다")
    void createExpiredRefreshTokenCookie_ShouldHaveSameSecurityAttributes() {
        // when
        Cookie expiredCookie = cookieService.createExpiredRefreshTokenCookie();
        Cookie normalCookie = cookieService.createRefreshTokenCookie(SAMPLE_REFRESH_TOKEN);

        // then
        assertThat(expiredCookie.isHttpOnly()).isEqualTo(normalCookie.isHttpOnly());
        assertThat(expiredCookie.getSecure()).isEqualTo(normalCookie.getSecure());
        assertThat(expiredCookie.getPath()).isEqualTo(normalCookie.getPath());
    }
}
