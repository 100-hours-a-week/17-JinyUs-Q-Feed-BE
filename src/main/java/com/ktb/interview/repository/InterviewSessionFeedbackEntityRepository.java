package com.ktb.interview.repository;

import com.ktb.interview.domain.entity.InterviewSessionFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionFeedbackEntityRepository extends JpaRepository<InterviewSessionFeedbackEntity, String> {
}
