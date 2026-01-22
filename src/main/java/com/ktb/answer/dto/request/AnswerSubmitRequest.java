package com.ktb.answer.dto.request;

import com.ktb.answer.domain.AnswerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 * 답변 제출 요청 DTO (연습/단일 답변)
 */
@Schema(description = "답변 제출 요청 (연습/단일 답변)")
public record AnswerSubmitRequest(

        @Schema(description = "질문 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "질문 ID는 필수입니다")
        Long questionId,

        @Schema(description = "답변 텍스트 (최대 5,000자, answerText 또는 audioFile 중 최소 1개 필수)",
                example = "프로세스는 실행 중인 프로그램의 인스턴스이며...",
                maxLength = 5000)
        @Size(max = MAX_ANSWER_TEXT_LENGTH, message = "답변 텍스트는 {max}자를 초과할 수 없습니다")
        String answerText,

        @Schema(description = "음성 파일 (최대 10MB, audio/* 형식)",
                type = "string",
                format = "binary")
        MultipartFile audioFile,

        @Schema(description = "답변 타입 (기본값: PRACTICE_INTERVIEW)",
                example = "PRACTICE_INTERVIEW",
                allowableValues = {"PRACTICE_INTERVIEW", "REAL_INTERVIEW", "PORTFOLIO_INTERVIEW"})
        AnswerType answerType
) {

    public static final int MAX_ANSWER_TEXT_LENGTH = 5_000;
    public static final long MAX_AUDIO_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public boolean hasAudio() {
        return audioFile != null && !audioFile.isEmpty();
    }

    public boolean hasText() {
        return answerText != null && !answerText.isBlank();
    }

    public boolean hasContent() {
        return hasText() || hasAudio();
    }
}
