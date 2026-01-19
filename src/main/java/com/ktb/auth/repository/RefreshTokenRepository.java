package com.ktb.auth.repository;

import com.ktb.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * RefreshToken Repository
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Token Hash로 조회 (Fetch Join with Family)
     */
    @Query("SELECT rt FROM RefreshToken rt " +
            "JOIN FETCH rt.family f " +
            "JOIN FETCH f.account " +
            "WHERE rt.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashWithFamily(@Param("tokenHash") String tokenHash);

    /**
     * Token Hash 존재 여부 확인
     */
    boolean existsByTokenHash(String tokenHash);
}
