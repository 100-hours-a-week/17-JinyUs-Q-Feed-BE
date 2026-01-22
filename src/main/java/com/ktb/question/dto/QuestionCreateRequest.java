package com.ktb.question.dto;

import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record QuestionCreateRequest(
        @NotBlank
        @Size(max = 200)
        String content,
        @NotNull
        QuestionType type,
        @NotNull
        QuestionCategory category,
        @Size(max = 5)
        List<@NotBlank @Size(max = 100) String> keywords
) {
}
