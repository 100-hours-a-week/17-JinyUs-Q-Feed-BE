package com.ktb.answer.dto;

import com.ktb.answer.domain.AnswerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public record AnswerSubmitCommand(
        @NotNull Long questionId,
        @Size(max = MAX_ANSWER_TEXT_LENGTH) String answerText,
        MultipartFile audioFile,
        AnswerType answerType
) {

    public static final int MAX_ANSWER_TEXT_LENGTH = 5_000;

    public boolean hasAudio() {
        return audioFile != null && !audioFile.isEmpty();
    }

    public boolean hasText() {
        return Objects.nonNull(answerText) && !answerText.isBlank();
    }
}
