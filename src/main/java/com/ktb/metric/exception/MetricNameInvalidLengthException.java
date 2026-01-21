package com.ktb.metric.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class MetricNameInvalidLengthException extends BusinessException {
    public MetricNameInvalidLengthException() {
        super(ErrorCode.METRIC_NAME_TOO_LONG);
    }
}
