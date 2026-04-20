package com.ktb.ai.feedback.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadCaseType {

    REFUSE_TO_ANSWER(
            "답변이 감지되지 않았습니다.",
            "질문에 대한 답변을 입력해주세요. 음성이 제대로 녹음되지 않았다면 다시 시도해주세요."
    ),

    TOO_SHORT(
            "답변이 너무 짧습니다.",
            "면접관이 이해할 수 있도록 더 자세하게 설명해주세요. 구체적인 예시나 근거를 포함하면 좋습니다."
    ),

    INAPPROPRIATE(
            "부적절한 답변이 감지되었습니다.",
            "질문과 관련된 기술적인 내용으로 답변해주세요."
    );

    private final String message;
    private final String guidance;
}
