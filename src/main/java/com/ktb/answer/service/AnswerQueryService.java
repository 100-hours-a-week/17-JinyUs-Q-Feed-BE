package com.ktb.answer.service;

import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.dto.AnswerDetailResult;
import com.ktb.answer.dto.response.list.AnswerListResponse;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import java.time.LocalDate;

public interface AnswerQueryService {

    /**
     * 답변 목록 조회 (본인 답변만)
     */
    AnswerListResponse getList(
            Long accountId,
            AnswerType type,
            QuestionCategory category,
            QuestionType questionType,
            LocalDate dateFrom,
            LocalDate dateTo,
            String cursor,
            Integer limit
    );

    /**
     * 답변 상세 조회 (본인 답변만)
     */
    AnswerDetailResult getDetail(Long accountId, Long answerId);
}
