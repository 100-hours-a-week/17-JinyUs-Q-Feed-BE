package com.ktb.answer.repository;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerType;
import com.ktb.question.domain.QuestionCategory;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /**
     * 답변 목록 조회 (본인 소유 + 필터링 + 커서 페이지네이션)
     * 정렬: created_at DESC, answer_id DESC
     */
    @Query("""
            SELECT a FROM Answer a
            JOIN FETCH a.question q
            WHERE a.deletedAt IS NULL
            AND a.account.id = :accountId
            AND (:type IS NULL OR a.type = :type)
            AND (:category IS NULL OR q.category = :category)
            AND (:dateFrom IS NULL OR a.createdAt >= :dateFrom)
            AND (:dateTo IS NULL OR a.createdAt <= :dateTo)
            AND (:cursorCreatedAt IS NULL OR a.createdAt < :cursorCreatedAt
                 OR (a.createdAt = :cursorCreatedAt AND a.id < :cursorAnswerId))
            ORDER BY a.createdAt DESC, a.id DESC
            """)
    Slice<Answer> findByAccountIdWithFilters(
            @Param("accountId") Long accountId,
            @Param("type") AnswerType type,
            @Param("category") QuestionCategory category,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorAnswerId") Long cursorAnswerId,
            Pageable pageable
    );

    /**
     * 답변 상세 조회 (본인 소유 확인용)
     */
    @Query("""
            SELECT a FROM Answer a
            JOIN FETCH a.question
            WHERE a.id = :answerId
            AND a.deletedAt IS NULL
            """)
    Answer findByIdWithQuestion(@Param("answerId") Long answerId);

    /**
     * 세션 내 중복 답변 체크
     * TODO: ANSWER_SESSION 엔티티 구현 후 활성화
     */
    // @Query("""
    //         SELECT COUNT(a) > 0 FROM Answer a
    //         WHERE a.session.id = :sessionId
    //         AND a.question.id = :questionId
    //         AND a.deletedAt IS NULL
    //         """)
    // boolean existsBySessionIdAndQuestionId(
    //         @Param("sessionId") String sessionId,
    //         @Param("questionId") Long questionId
    // );
}
