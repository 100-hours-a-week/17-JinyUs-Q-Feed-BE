package com.ktb.auth.repository;

import com.ktb.auth.domain.TokenFamily;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenFamilyRepository extends JpaRepository<TokenFamily, Long> {

    Optional<TokenFamily> findByUuid(String uuid);

    /**
     * 계정의 모든 Family 무효화 (로그아웃 전체)
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE TokenFamily tf " +
            "SET tf.revoked = true, " +
            "tf.revokedAt = CURRENT_TIMESTAMP, " +
            "tf.revokedReason = :reason " +
            "WHERE tf.account.id = :accountId " +
            "AND tf.revoked = false")
    int revokeAllByAccountId(
            @Param("accountId") Long accountId,
            @Param("reason") com.ktb.auth.domain.RevokeReason reason
    );
}
