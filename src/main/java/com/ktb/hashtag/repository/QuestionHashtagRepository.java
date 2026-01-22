package com.ktb.hashtag.repository;

import com.ktb.hashtag.domain.QuestionHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionHashtagRepository extends JpaRepository<QuestionHashtag, Long> {

    @Query("""
            SELECT DISTINCT h.name
            FROM QuestionHashtag qh
            JOIN qh.hashtag h
            WHERE qh.question.id = :questionId
            """)
    List<String> findKeywordNamesByQuestionId(@Param("questionId") Long questionId);

    boolean existsByQuestion_IdAndHashtag_NameIgnoreCase(Long questionId, String name);

    void deleteByQuestion_Id(Long questionId);

    @Query("""
            SELECT qh.question.id AS questionId, h.name AS keyword
            FROM QuestionHashtag qh
            JOIN qh.hashtag h
            WHERE qh.question.id IN :questionIds
            """)
    List<QuestionKeywordRow> findKeywordRowsByQuestionIdIn(@Param("questionIds") List<Long> questionIds);
}
