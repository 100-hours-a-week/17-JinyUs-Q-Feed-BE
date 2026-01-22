package com.ktb.answer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 * 실전 세션 기반 답변 제출 요청 DTO
 */
@Schema(description = "실전 세션 기반 답변 제출 요청")
public record SessionAnswerSubmitRequest(

        @Schema(description = "질문 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "질문 ID는 필수입니다")
        Long questionId,

        @Schema(description = "답변 텍스트 (최대 5,000자, 필수)",
                example = "프로세스는 실행 중인 프로그램의 인스턴스입니다...",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 5000)
        @NotBlank(message = "답변 텍스트는 필수입니다")
        @Size(max = MAX_ANSWER_TEXT_LENGTH, message = "답변 텍스트는 {max}자를 초과할 수 없습니다")
        String answerText,

        @Schema(description = "비디오 파일 (최대 100MB, video/* 형식, 선택)",
                type = "string",
                format = "binary")
        MultipartFile videoFile
) {

    public static final int MAX_ANSWER_TEXT_LENGTH = 5_000;
    public static final long MAX_VIDEO_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    public boolean hasVideo() {
        return videoFile != null && !videoFile.isEmpty();
    }
}
