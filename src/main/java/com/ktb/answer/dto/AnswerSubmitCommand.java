package com.ktb.answer.dto;

import com.ktb.answer.domain.AnswerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public record AnswerSubmitCommand(
        @NotBlank Long questionId,
        @Size(max = MAX_ANSWER_TEXT_LENGTH) String answerText,
        AnswerType answerType
) {

    public static final int MAX_ANSWER_TEXT_LENGTH = 1_500;
}
