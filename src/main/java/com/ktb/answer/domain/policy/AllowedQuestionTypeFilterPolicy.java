package com.ktb.answer.domain.policy;

import com.ktb.question.domain.QuestionType;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AllowedQuestionTypeFilterPolicy implements QuestionTypeFilterPolicy {

    private static final Set<QuestionType> ALLOWED = Set.of(
        QuestionType.CS, QuestionType.SYSTEM_DESIGN
    );

    @Override
    public boolean isSatisfiedBy(QuestionType type) {
        return ALLOWED.contains(type);
    }
}
