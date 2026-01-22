package com.ktb.ai.feedback.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.ai.feedback.dto.request.AiFeedbackRequest;
import com.ktb.ai.feedback.dto.response.AiFeedbackApiResponse;
import com.ktb.ai.feedback.exception.AiFeedbackAlreadyInProgressException;
import com.ktb.ai.feedback.exception.AiFeedbackAnswerTooLongException;
import com.ktb.ai.feedback.exception.AiFeedbackAnswerTooShortException;
import com.ktb.ai.feedback.exception.AiFeedbackEmptyAnswerException;
import com.ktb.ai.feedback.exception.AiFeedbackEmptyQuestionException;
import com.ktb.ai.feedback.exception.AiFeedbackInternalServerException;
import com.ktb.ai.feedback.exception.AiFeedbackLlmServiceUnavailableException;
import com.ktb.ai.feedback.exception.AiFeedbackRateLimitException;
import com.ktb.ai.feedback.exception.AiFeedbackServiceException;
import com.ktb.ai.feedback.exception.AiFeedbackServiceTemporarilyUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiFeedbackClient {

    private static final String MESSAGE_EMPTY_QUESTION = "EMPTY_QUESTION";
    private static final String MESSAGE_EMPTY_ANSWER = "EMPTY_ANSWER";
    private static final String MESSAGE_ANSWER_TOO_SHORT = "ANSWER_TOO_SHORT";
    private static final String MESSAGE_ANSWER_TOO_LONG = "ANSWER_TOO_LONG";

    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.feedback.base-url}")
    private String baseUrl;

    @Value("${ai.feedback.endpoint}")
    private String endpoint;

    public AiFeedbackApiResponse evaluate(AiFeedbackRequest request) {
        String url = baseUrl + endpoint;

        log.info("Requesting AI feedback - URL: {}, userId: {}, questionId: {}",
                url, request.userId(), request.questionId());

        try {
            AiFeedbackApiResponse response = aiRestClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        int statusCode = res.getStatusCode().value();
                        String responseBody = new String(res.getBody().readAllBytes());

                        log.error("AI Feedback 4xx error - status: {}, body: {}", statusCode, responseBody);

                        if (statusCode == 400) {
                            handle400Error(responseBody);
                        } else if (statusCode == 409) {
                            throw new AiFeedbackAlreadyInProgressException(
                                    "이미 AI 피드백 생성이 진행 중입니다");
                        } else if (statusCode == 429) {
                            throw new AiFeedbackRateLimitException(
                                    "AI 피드백 요청 한도를 초과했습니다");
                        } else {
                            throw new AiFeedbackServiceException(
                                    "AI 피드백 요청 오류 - status: " + statusCode);
                        }
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        int statusCode = res.getStatusCode().value();
                        String responseBody = new String(res.getBody().readAllBytes());

                        log.error("AI Feedback 5xx error - status: {}, body: {}", statusCode, responseBody);

                        if (statusCode == 500) {
                            throw new AiFeedbackInternalServerException(
                                    "AI 서버 내부 오류가 발생했습니다");
                        } else if (statusCode == 502) {
                            throw new AiFeedbackLlmServiceUnavailableException(
                                    "LLM 서비스 연결에 실패했습니다");
                        } else if (statusCode == 503) {
                            throw new AiFeedbackServiceTemporarilyUnavailableException(
                                    "AI 피드백 서비스를 일시적으로 사용할 수 없습니다");
                        } else {
                            throw new AiFeedbackServiceException(
                                    "AI 피드백 서버 오류 - status: " + statusCode);
                        }
                    })
                    .body(AiFeedbackApiResponse.class);

            if (response == null) {
                throw new AiFeedbackServiceException("AI 피드백 응답이 null입니다");
            }

            if (!response.isSuccess()) {
                log.error("AI feedback request failed - errorMessage: {}", response.errorMessage());
                throw new AiFeedbackServiceException(
                        "AI 피드백 생성 실패: " + response.errorMessage()
                );
            }

            log.info("AI feedback request successful - userId: {}, questionId: {}",
                    request.userId(), request.questionId());

            return response;

        } catch (RestClientException e) {
            log.error("AI feedback API call failed - URL: {}, error: {}", url, e.getMessage(), e);
            throw new AiFeedbackServiceException("AI 피드백 서버 호출 실패", e);
        }
    }

    private void handle400Error(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "";

            switch (message) {
                case MESSAGE_EMPTY_QUESTION:
                    throw new AiFeedbackEmptyQuestionException("질문 텍스트가 비어있습니다");
                case MESSAGE_EMPTY_ANSWER:
                    throw new AiFeedbackEmptyAnswerException("답변 텍스트가 비어있습니다");
                case MESSAGE_ANSWER_TOO_SHORT:
                    throw new AiFeedbackAnswerTooShortException("답변 길이가 부족합니다");
                case MESSAGE_ANSWER_TOO_LONG:
                    throw new AiFeedbackAnswerTooLongException("답변 길이가 초과되었습니다");
                default:
                    throw new AiFeedbackServiceException(
                            "AI 피드백 요청 오류 - message: " + message);
            }
        } catch (IOException e) {
            log.error("Failed to parse error response body: {}", responseBody, e);
            throw new AiFeedbackServiceException("AI 피드백 에러 응답 파싱 실패", e);
        }
    }
}
