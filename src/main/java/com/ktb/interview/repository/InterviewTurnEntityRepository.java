package com.ktb.interview.repository;

import com.ktb.interview.domain.entity.InterviewTurnEntity;
import com.ktb.interview.domain.entity.InterviewTurnId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewTurnEntityRepository extends JpaRepository<InterviewTurnEntity, InterviewTurnId> {
    void deleteByIdSessionId(String sessionId);

    List<InterviewTurnEntity> findByIdSessionIdOrderByIdTurnOrderAsc(String sessionId);
}
