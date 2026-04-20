package com.ktb.answer.domain.policy;

import com.ktb.answer.exception.AnswerListInvalidInputException;
import com.ktb.question.domain.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AllowedQuestionTypeFilterPolicy 단위 테스트")
class AllowedQuestionTypeFilterPolicyTest {

    private final AllowedQuestionTypeFilterPolicy policy = new AllowedQuestionTypeFilterPolicy();

    @Test
    @DisplayName("CS는 허용된다")
    void isSatisfiedBy_CS_returnsTrue() {
        assertThat(policy.isSatisfiedBy(QuestionType.CS)).isTrue();
    }

    @Test
    @DisplayName("PORTFOLIO 허용되지 않는다")
    void isSatisfiedBy_Portfolio_returnsFalse() {
        assertThat(policy.isSatisfiedBy(QuestionType.PORTFOLIO)).isFalse();
    }

    @Test
    @DisplayName("SYSTEM_DESIGN 허용된다")
    void isSatisfiedBy_SystemDesign_returnsTrue() {
        assertThat(policy.isSatisfiedBy(QuestionType.SYSTEM_DESIGN)).isTrue();
    }

    @Test
    @DisplayName("null 타입은 예외 없이 통과한다")
    void validateOrThrow_nullType_doesNotThrow() {
        assertThatNoException().isThrownBy(() -> policy.validateOrThrow(null));
    }

    @Test
    @DisplayName("허용된 타입은 예외 없이 통과한다")
    void validateOrThrow_allowedType_doesNotThrow() {
        assertThatNoException().isThrownBy(() -> policy.validateOrThrow(QuestionType.CS));
        assertThatNoException().isThrownBy(() -> policy.validateOrThrow(QuestionType.SYSTEM_DESIGN));
    }

    @Test
    @DisplayName("PORTFOLIO 타입은 AnswerListInvalidInputException을 던진다")
    void validateOrThrow_portfolio_throwsException() {
        assertThatThrownBy(() -> policy.validateOrThrow(QuestionType.PORTFOLIO))
            .isInstanceOf(AnswerListInvalidInputException.class);
    }
}
