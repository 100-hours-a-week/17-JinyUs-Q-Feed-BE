package com.ktb.file.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 생성 응답")
public record PresignedUrlResponse(

        @JsonProperty("file_id")
        @Schema(description = "생성된 파일 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long fileId,

        @JsonProperty("presigned_url")
        @Schema(description = "S3 Presigned URL",
                example = "https://s3.amazonaws.com/bucket/uploads/audio/uuid.mp3?X-Amz-Algorithm=...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String presignedUrl,

        @JsonProperty("expires_in")
        @Schema(description = "URL 만료 시간 (초)", example = "300", requiredMode = Schema.RequiredMode.REQUIRED)
        int expiresIn,

        @JsonProperty("method")
        @Schema(description = "HTTP 메서드", example = "PUT", requiredMode = Schema.RequiredMode.REQUIRED)
        String method
) {
}
