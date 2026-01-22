package com.ktb.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record QuestionKeywordCheckRequest(
        @NotEmpty
        @Size(max = 5)
        List<@NotBlank @Size(max = 100) String> keywords
) {
}
