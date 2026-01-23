package com.ktb.hashtag.repository;

import com.ktb.hashtag.domain.AnswerHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerHashtagRepository extends JpaRepository<AnswerHashtag, Long> {

    /**
     * 답변 삭제 시 AnswerHashtag 함께 삭제
     */
    void deleteByAnswerId(Long answerId);
}
