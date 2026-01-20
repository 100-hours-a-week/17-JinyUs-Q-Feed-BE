package com.ktb.auth.security.adapter;

import com.ktb.auth.security.abstraction.RequestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BearerTokenExtractor 순수 단위 테스트
 * - HttpServletRequest Mock 불필요
 * - 간단한 람다로 RequestContext 구현
 */
@DisplayName("BearerTokenExtractor 단위 테스트")
class BearerTokenExtractorTest {

    private final BearerTokenExtractor extractor = new BearerTokenExtractor();

    @Test
    @DisplayName("Bearer 토큰 추출 성공")
    void extractToken_WithBearerHeader_ShouldReturnToken() {
        // given
        RequestContext context = header -> "Bearer my.jwt.token";

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).hasValue("my.jwt.token");
    }

    @Test
    @DisplayName("Bearer 접두사 없으면 빈 Optional 반환")
    void extractToken_WithoutBearer_ShouldReturnEmpty() {
        // given
        RequestContext context = header -> "InvalidFormat token";

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("Authorization 헤더 없으면 빈 Optional 반환")
    void extractToken_WithoutAuthHeader_ShouldReturnEmpty() {
        // given
        RequestContext context = header -> null;

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("빈 Authorization 헤더면 빈 Optional 반환")
    void extractToken_WithEmptyAuthHeader_ShouldReturnEmpty() {
        // given
        RequestContext context = header -> "";

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("Bearer만 있고 토큰 없으면 빈 문자열 반환")
    void extractToken_WithBearerOnly_ShouldReturnEmptyString() {
        // given
        RequestContext context = header -> "Bearer ";

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).hasValue("");
    }

    @Test
    @DisplayName("대소문자 구분: bearer로 시작하면 실패")
    void extractToken_WithLowercaseBearer_ShouldReturnEmpty() {
        // given
        RequestContext context = header -> "bearer my.jwt.token";

        // when
        Optional<String> token = extractor.extractToken(context);

        // then
        assertThat(token).isEmpty();
    }
}
