package com.ktb.metric.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class MetricRequiredNameException extends BusinessException {
    public MetricRequiredNameException() {
        super(ErrorCode.METRIC_NAME_REQUIRED);
    }
}
