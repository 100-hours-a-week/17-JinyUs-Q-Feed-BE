package com.ktb.answer.domain.policy;

import com.ktb.answer.exception.AnswerListInvalidInputException;
import com.ktb.question.domain.QuestionType;

public interface QuestionTypeFilterPolicy {

    boolean isSatisfiedBy(QuestionType type);

    default void validateOrThrow(QuestionType type) {
        if (type != null && !isSatisfiedBy(type)) {
            throw new AnswerListInvalidInputException("지원하지 않는 questionType: " + type.name());
        }
    }
}
