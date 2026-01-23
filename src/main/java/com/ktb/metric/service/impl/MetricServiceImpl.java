package com.ktb.metric.service.impl;

import com.ktb.metric.domain.Metric;
import com.ktb.metric.dto.MetricCreateRequest;
import com.ktb.metric.dto.MetricDetailResponse;
import com.ktb.metric.dto.MetricListResponse;
import com.ktb.metric.dto.MetricPaginationResponse;
import com.ktb.metric.dto.MetricSummaryResponse;
import com.ktb.metric.dto.MetricUpdateRequest;
import com.ktb.metric.exception.MetricNotFoundException;
import com.ktb.metric.repository.MetricRepository;
import com.ktb.metric.service.MetricService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricServiceImpl implements MetricService {

    private final MetricRepository metricRepository;

    @Override
    public MetricListResponse getMetrics(Boolean useYn, Long cursor, int size) {
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Metric> metrics = findMetrics(useYn, cursor, pageable);
        return new MetricListResponse(
                metrics.getContent().stream()
                        .map(this::toSummaryResponse)
                        .toList(),
                toPaginationResponse(metrics)
        );
    }

    @Override
    public MetricDetailResponse getMetric(Long metricId) {
        Metric metric = metricRepository.findById(metricId)
                .orElseThrow(() -> new MetricNotFoundException(metricId));
        return toDetailResponse(metric);
    }

    @Override
    @Transactional
    public MetricDetailResponse createMetric(MetricCreateRequest request) {
        Metric metric = Metric.create(request.name(), request.description());
        Metric saved = metricRepository.save(metric);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public MetricDetailResponse updateMetric(Long metricId, MetricUpdateRequest request) {
        Metric metric = metricRepository.findById(metricId)
                .orElseThrow(() -> new MetricNotFoundException(metricId));

        if (request.name() != null) {
            metric.updateName(request.name());
        }
        if (request.description() != null) {
            metric.updateDescription(request.description());
        }
        if (request.useYn() != null) {
            if (request.useYn()) {
                metric.enable();
            } else {
                metric.disable();
            }
        }

        return toDetailResponse(metric);
    }

    @Override
    @Transactional
    public void deleteMetric(Long metricId) {
        Metric metric = metricRepository.findById(metricId)
                .orElseThrow(() -> new MetricNotFoundException(metricId));
        metric.disable();
    }

    private MetricSummaryResponse toSummaryResponse(Metric metric) {
        return new MetricSummaryResponse(
                metric.getId(),
                metric.getName(),
                metric.getDescription(),
                metric.isUseYn()
        );
    }

    private MetricDetailResponse toDetailResponse(Metric metric) {
        return new MetricDetailResponse(
                metric.getId(),
                metric.getName(),
                metric.getDescription(),
                metric.isUseYn(),
                metric.getCreatedAt(),
                metric.getUpdatedAt()
        );
    }

    private MetricPaginationResponse toPaginationResponse(Slice<Metric> slice) {
        Long nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            Metric last = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = slice.hasNext() ? last.getId() : null;
        }
        return new MetricPaginationResponse(nextCursor, slice.hasNext(), slice.getSize());
    }

    private Slice<Metric> findMetrics(Boolean useYn, Long cursor, PageRequest pageable) {
        if (cursor == null) {
            return (useYn == null)
                    ? metricRepository.findAll(pageable)
                    : metricRepository.findByUseYn(useYn, pageable);
        }
        return (useYn == null)
                ? metricRepository.findByIdLessThan(cursor, pageable)
                : metricRepository.findByUseYnAndIdLessThan(useYn, cursor, pageable);
    }
}
