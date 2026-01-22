package com.ktb.hashtag.domain;

import com.ktb.common.domain.BaseTimeEntity;
import com.ktb.question.domain.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "QUESTION_HASHTAG",
        indexes = {
                @Index(name = "uk_question_hashtag", columnList = "question_id, tag_id", unique = true),
                @Index(name = "idx_question_id", columnList = "question_id"),
                @Index(name = "idx_tag_id", columnList = "tag_id"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"question", "hashtag"})
public class QuestionHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Hashtag hashtag;

    @Builder
    private QuestionHashtag(Question question, Hashtag hashtag) {
        this.question = question;
        this.hashtag = hashtag;
    }

    public static QuestionHashtag create(Question question, Hashtag hashtag) {
        return QuestionHashtag.builder()
                .question(question)
                .hashtag(hashtag)
                .build();
    }
}
