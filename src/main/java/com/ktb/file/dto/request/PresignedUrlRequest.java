package com.ktb.file.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktb.file.domain.FileCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Presigned URL 생성 요청")
public record PresignedUrlRequest(

        @JsonProperty("file_name")
        @Schema(description = "파일명", example = "interview_answer.mp3", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "파일명은 필수입니다")
        String fileName,

        @JsonProperty("file_size")
        @Schema(description = "파일 크기 (bytes)", example = "5242880", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "파일 크기는 필수입니다")
        @Min(value = 1, message = "파일 크기는 1바이트 이상이어야 합니다")
        Long fileSize,

        @JsonProperty("mime_type")
        @Schema(description = "MIME 타입", example = "audio/mpeg", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "MIME 타입은 필수입니다")
        String mimeType,

        @JsonProperty("category")
        @Schema(description = "파일 카테고리", example = "AUDIO", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PROFILE", "ARCHITECTURE", "ATTACHMENT", "TEMP", "AUDIO", "VIDEO"})
        @NotNull(message = "파일 카테고리는 필수입니다")
        FileCategory category
) {
}
