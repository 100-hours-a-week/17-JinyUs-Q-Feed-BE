package com.ktb.answer.repository;

import com.ktb.answer.domain.AnswerFeedbackInbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerFeedbackInboxRepository extends JpaRepository<AnswerFeedbackInbox, Long> {

    boolean existsByEventId(String eventId);
}
