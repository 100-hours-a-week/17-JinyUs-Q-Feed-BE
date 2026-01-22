package com.ktb.question.domain;

import com.ktb.common.domain.BaseActivatableEntity;
import com.ktb.question.exception.QuestionAlreadyDeletedException;
import com.ktb.question.exception.QuestionInvalidContentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "QUESTION",
        indexes = {}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Question extends BaseActivatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "question_content", nullable = false, length = 200)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type_cd", nullable = false, length = 50)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_ctg", nullable = false, length = 50)
    private QuestionCategory category;

    @Builder
    private Question(String content, QuestionType type, QuestionCategory category) {
        validateContent(content);
        this.content = content;
        this.type = type;
        this.category = category;
    }

    public static Question create(
            String content,
            QuestionType type,
            QuestionCategory category
    ) {
        return Question.builder()
                .content(content)
                .type(type)
                .category(category)
                .build();
    }

    public void updateType(QuestionType type) {
        this.type = type;
    }

    public void updateCategory(QuestionCategory category) {
        this.category = category;
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void delete() {
        if (isEnabled()) {
            throw new QuestionAlreadyDeletedException(id);
        }
        disable();
        softDelete();
    }

    public void activate() {
        enable();
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new QuestionInvalidContentException("질문 내용은 필수입니다.");
        }
        if (content.length() > 200) {
            throw new QuestionInvalidContentException("질문 내용은 200자를 초과할 수 없습니다.");
        }
    }
}
