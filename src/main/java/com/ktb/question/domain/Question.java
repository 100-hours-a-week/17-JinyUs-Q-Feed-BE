package com.ktb.question.domain;

import com.ktb.common.domain.BaseTimeEntity;
import com.ktb.question.exception.QuestionAlreadyDeletedException;
import com.ktb.question.exception.QuestionInvalidContentException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 면접 질문 엔티티
 * - Soft Delete 방식 채택
 */
@Entity
@Table(
        name = "QUESTION",
        indexes = {}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Question extends BaseTimeEntity {
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;

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
        Question question = Question.builder()
                .content(content)
                .type(type)
                .category(category)
                .build();

        return question;
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
        if (!useYn || deletedAt != null) {
            throw new QuestionAlreadyDeletedException(id);
        }
        this.useYn = false;
        this.deletedAt = LocalDateTime.now();
    }

    public void activate() {
        this.useYn = true;
        this.deletedAt = null;
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
