package com.ktb.metric.repository;

import com.ktb.metric.domain.Metric;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    Slice<Metric> findByUseYn(boolean useYn, Pageable pageable);

    Slice<Metric> findByIdLessThan(Long cursor, Pageable pageable);

    Slice<Metric> findByUseYnAndIdLessThan(boolean useYn, Long cursor, Pageable pageable);
}
