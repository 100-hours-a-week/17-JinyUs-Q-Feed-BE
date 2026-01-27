package com.ktb.ai.stt.client;

import com.ktb.ai.stt.dto.request.SttRequest;
import com.ktb.ai.stt.dto.response.SttResponse;
import com.ktb.ai.stt.exception.*;
import com.ktb.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SttClient {

    private static final int STATUS_AUDIO_TOO_LONG_OR_TOO_LARGE = 400;
    private static final int STATUS_AUDIO_NOT_FOUND = 404;
    private static final int STATUS_AUDIO_UNPROCESSABLE = 422;
    private static final int STATUS_STT_TIMEOUT = 408;
    private static final int STATUS_RATE_LIMIT_EXCEEDED = 429;

    private final RestClient aiRestClient;

    @Value("${ai.stt.base-url}")
    private String baseUrl;

    @Value("${ai.stt.endpoint}")
    private String endpoint;

    /**
     * STT 변환 요청
     *
     * @param request STT 요청 DTO
     * @return 변환된 텍스트
     * @throws SttServiceException           STT 서버 호출 실패 시
     * @throws AudioTooLongException         오디오 길이 초과
     * @throws AudioTooLargeException        파일 크기 초과
     * @throws AudioNotFoundException        오디오 파일 없음
     * @throws AudioUnprocessableException   오디오 처리 불가
     * @throws SttTimeoutException           변환 시간 초과
     */
    public String convert(SttRequest request) {
        String url = baseUrl + endpoint;

        log.info("Requesting STT conversion - URL: {}, userId: {}, sessionId: {}, audioUrl: {}",
                url, request.userId(), request.sessionId(), request.audioUrl());

        try {
            ApiResponse<SttResponse> response = aiRestClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        handleClientError(res.getStatusCode().value(), request.audioUrl());
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

            boolean isNullResponse =
                (response != null && response.data() != null)
                    && response.data().text() != null;
            String text =  isNullResponse ? response.data().text() : "";

            log.info("STT conversion successful - userId: {}, sessionId: {}, textLength: {}",
                    request.userId(), request.sessionId(), text.length());

            return text;

        } catch (RestClientException e) {
            log.error("STT API call failed - URL: {}, error: {}", url, e.getMessage(), e);
            throw new SttServiceException("STT 서버 호출 실패", e);
        }
    }

    /**
     * 4xx 클라이언트 에러 처리
     *
     * @param statusCode HTTP 상태 코드
     * @param audioUrl   오디오 URL
     */
    private void handleClientError(int statusCode, String audioUrl) {
        log.error("STT client error - statusCode: {}, audioUrl: {}", statusCode, audioUrl);

        switch (statusCode) {
            case STATUS_AUDIO_TOO_LONG_OR_TOO_LARGE:
                throw new AudioTooLongException("오디오 길이가 최대 허용 길이(5분) 또는 파일 크기가 최대 허용 크기(25MB)를 초과했습니다");
            case STATUS_AUDIO_NOT_FOUND:
                throw new AudioNotFoundException(audioUrl);
            case STATUS_STT_TIMEOUT:
                throw new SttTimeoutException("STT 처리 시간이 초과되었습니다");
            case STATUS_AUDIO_UNPROCESSABLE:
                throw new AudioUnprocessableException("오디오 파일이 손상되었거나 인식할 수 없습니다");
            case STATUS_RATE_LIMIT_EXCEEDED:
                throw new SttServiceException("STT 요청 한도를 초과했습니다");
            default:
                throw new SttServiceException("STT 클라이언트 오류: " + statusCode);
        }
    }
}
