package com.ktb.ai.stt.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "STT 변환 데이터")
public record SttData(

        @JsonProperty("text")
        @Schema(description = "변환된 텍스트",
                example = "RDBMS는 엄격한 스키마가 존재하고 SQL을 사용해 데이터를 관리하는 반면...")
        String text
) {
}
