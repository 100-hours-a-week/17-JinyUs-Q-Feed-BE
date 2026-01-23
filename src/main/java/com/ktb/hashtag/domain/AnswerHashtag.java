package com.ktb.hashtag.domain;

import com.ktb.answer.domain.Answer;
import com.ktb.common.domain.BaseTimeEntity;
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
        name = "ANSWER_HASHTAG",
        indexes = {
                @Index(name = "uk_answer_hashtag", columnList = "answer_id, tag_id", unique = true),
                @Index(name = "idx_answer_id", columnList = "answer_id"),
                @Index(name = "idx_tag_id", columnList = "tag_id"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"answer", "hashtag"})
public class AnswerHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Hashtag hashtag;

    @Column(name = "keyword_included")
    private boolean included;

    @Builder
    private AnswerHashtag(Answer answer, Hashtag hashtag, boolean included) {
        this.answer = answer;
        this.hashtag = hashtag;
        this.included = included;
    }

    public static AnswerHashtag create(Answer answer, Hashtag hashtag, boolean included) {
        return AnswerHashtag.builder()
                .answer(answer)
                .hashtag(hashtag)
                .included(included)
                .build();
    }
}
