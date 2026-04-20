package com.ktb.answer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "answer_feedback_inbox",
    uniqueConstraints = @UniqueConstraint(columnNames = "event_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerFeedbackInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public AnswerFeedbackInbox(String eventId, Long answerId) {
        this.eventId = eventId;
        this.answerId = answerId;
        this.processedAt = Instant.now();
    }
}
