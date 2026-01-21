package com.ktb.metric.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class MetricInvalidRangeException extends BusinessException {
    public MetricInvalidRangeException() {
        super(ErrorCode.METRIC_SCORE_INVALID_RANGE);
    }
}
