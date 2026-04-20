package com.ktb.answer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ktb.answer.dto.AnswerListCursor;
import com.ktb.answer.exception.AnswerListInvalidInputException;
import java.io.IOException;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class CursorCodec {

    private final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public AnswerListCursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(cursor);
            AnswerListCursor payload = jsonMapper.readValue(decoded, AnswerListCursor.class);
            if (payload.lastCreatedAt() == null || payload.lastAnswerId() == null) {
                throw new AnswerListInvalidInputException("cursor payload is incomplete");
            }
            return payload;
        } catch (IllegalArgumentException | IOException e) {
            throw new AnswerListInvalidInputException("invalid cursor", e);
        }
    }

    public String encode(AnswerListCursor cursor) {
        try {
            byte[] json = jsonMapper.writeValueAsBytes(cursor);
            return Base64.getEncoder().encodeToString(json);
        } catch (JsonProcessingException e) {
            throw new AnswerListInvalidInputException("failed to encode cursor", e);
        }
    }
}
