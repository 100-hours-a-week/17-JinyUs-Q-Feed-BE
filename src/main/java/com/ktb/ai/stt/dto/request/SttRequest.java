package com.ktb.ai.stt.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "STT 변환 요청")
public record SttRequest(

        @JsonProperty("user_id")
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @JsonProperty("session_id")
        @Schema(description = "면접 세션 식별자", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long sessionId,

        @JsonProperty("audio_url")
        @Schema(description = "S3에 저장된 음성 파일 주소",
                example = "https://qfeed-files.s3.amazonaws.com/uploads/audio/uuid.mp3",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "오디오 URL은 필수입니다")
        String audioUrl
) {
}
