package com.ktb.interview.repository;

import com.ktb.interview.domain.entity.InterviewSessionMetricEntity;
import com.ktb.interview.domain.entity.InterviewSessionMetricId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionMetricEntityRepository extends JpaRepository<InterviewSessionMetricEntity, InterviewSessionMetricId> {
    void deleteByIdSessionId(String sessionId);

    List<InterviewSessionMetricEntity> findByIdSessionIdOrderByIdMetricIdAsc(String sessionId);
}
