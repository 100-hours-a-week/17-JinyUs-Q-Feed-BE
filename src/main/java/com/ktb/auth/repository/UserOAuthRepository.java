package com.ktb.auth.repository;

import com.ktb.auth.domain.OAuthProvider;
import com.ktb.auth.domain.UserOAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UserOAuth Repository
 */
@Repository
public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {

    /**
     * OAuth 제공자와 제공자 사용자 ID로 조회 (Fetch Join)
     */
    @Query("SELECT uo FROM UserOAuth uo " +
            "JOIN FETCH uo.account " +
            "WHERE uo.provider = :provider " +
            "AND uo.providerUserId = :providerUserId " +
            "AND uo.unlinkedAt IS NULL")
    Optional<UserOAuth> findByProviderAndProviderUserIdWithAccount(
            @Param("provider") OAuthProvider provider,
            @Param("providerUserId") String providerUserId
    );
}
