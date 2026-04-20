package com.ktb.ai.feedback.service.impl;

import com.ktb.ai.feedback.client.AiFeedbackClient;
import com.ktb.ai.feedback.dto.request.AiFeedbackRequest;
import com.ktb.ai.feedback.dto.response.AiFeedbackResponse;
import com.ktb.ai.feedback.service.AiFeedbackService;
import com.ktb.answer.domain.AnswerType;
import com.ktb.common.dto.ApiResponse;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFeedbackServiceImpl implements AiFeedbackService {

    private final AiFeedbackClient aiFeedbackClient;

    @Override
    public ApiResponse<AiFeedbackResponse> evaluateSync(
            Long userId,
            Long questionId,
            QuestionType type,
            QuestionCategory category,
            AnswerType answerType,
            String questionContent,
            String answerContent
    ) {
        log.debug("Evaluating AI feedback - userId: {}, questionId: {}, type: {}, category: {}",
                userId, questionId, type, category);

        String categoryName = category != null ? category.name() : null;
        AiFeedbackRequest request = new AiFeedbackRequest(
                userId,
                questionId,
                type.name(),
                categoryName,
                answerType.name(),
                questionContent,
                answerContent
        );

        ApiResponse<AiFeedbackResponse> response = aiFeedbackClient.evaluate(request);

        log.info("AI feedback evaluation completed - userId: {}, questionId: {}", userId, questionId);

        return response;
    }

}
