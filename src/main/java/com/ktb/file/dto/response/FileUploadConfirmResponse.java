package com.ktb.file.dto.response;

import com.ktb.file.domain.FileUploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드 완료 확인 응답")
public record FileUploadConfirmResponse(

        @Schema(description = "파일 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long fileId,

        @Schema(description = "파일 URL (Presigned GET)", example = "https://s3.amazonaws.com/bucket/uploads/audio/uuid.mp3?X-Amz-Algorithm=...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String fileUrl,

        @Schema(description = "업로드 상태", example = "UPLOADED", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "UPLOADED", "FAILED"})
        FileUploadStatus status
) {
}
