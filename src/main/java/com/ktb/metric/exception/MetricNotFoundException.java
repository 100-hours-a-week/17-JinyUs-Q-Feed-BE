package com.ktb.metric.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class MetricNotFoundException extends BusinessException {
    public MetricNotFoundException(Long metricId) {
        super(ErrorCode.METRIC_NOT_FOUND,
                String.format("%s id=%d", ErrorCode.METRIC_NOT_FOUND.getMessage(), metricId));
    }
}
