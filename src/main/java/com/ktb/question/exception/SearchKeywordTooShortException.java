package com.ktb.question.exception;

import com.ktb.common.domain.ErrorCode;
import com.ktb.common.exception.BusinessException;

public class SearchKeywordTooShortException extends BusinessException {

    public SearchKeywordTooShortException(int minLength) {
        super(ErrorCode.SEARCH_KEYWORD_TOO_SHORT,
                String.format("검색어는 최소 %d자 이상이어야 합니다.", minLength));
    }
}
