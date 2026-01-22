package com.ktb.file.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "파일 업로드 완료 확인 요청")
public record FileUploadConfirmRequest(

        @Schema(description = "파일 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "파일 ID는 필수입니다")
        Long fileId
) {
}
