package com.ktb.hashtag.repository;

/**
 * N+1 방지 목적 경량 조회 인터페이스
 */
public interface QuestionKeywordRow {
    Long getQuestionId();

    String getKeyword();
}
