package com.ktb.common.config;

import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.security.adapter.SecurityUserAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * {@link WithMockCustomUser} 어노테이션을 처리하여 SecurityContext를 생성하는 팩토리 클래스
 *
 * <p>테스트 실행 시 SecurityContext에 {@link SecurityUserAccount}를 주입하여
 * 실제 인증된 사용자처럼 동작하도록 만듭니다.
 *
 * <p>생성되는 {@link UserAccount}는 {@link SecurityUserAccount}로 래핑되어
 * Spring Security의 Principal로 사용됩니다.
 */
public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Annotation에서 값을 읽어 UserAccount 생성
        UserAccount userAccount = UserAccount.builder()
                .email(annotation.email())
                .nickname(annotation.nickname())
                .build();

        // ID 설정을 위한 리플렉션 (테스트용)
        try {
            java.lang.reflect.Field idField = UserAccount.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userAccount, annotation.userId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID for test", e);
        }

        // SecurityUserAccount로 래핑
        SecurityUserAccount principal = new SecurityUserAccount(userAccount);

        // Authentication 객체 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,  // credentials
                principal.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}
