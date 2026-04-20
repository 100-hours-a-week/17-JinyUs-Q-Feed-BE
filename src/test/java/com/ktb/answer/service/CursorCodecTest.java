package com.ktb.answer.service;

import com.ktb.answer.dto.AnswerListCursor;
import com.ktb.answer.exception.AnswerListInvalidInputException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CursorCodec 단위 테스트")
class CursorCodecTest {

    private final CursorCodec cursorCodec = new CursorCodec();

    @Test
    @DisplayName("null cursor는 null을 반환한다")
    void decode_nullCursor_returnsNull() {
        assertThat(cursorCodec.decode(null)).isNull();
    }

    @Test
    @DisplayName("빈 cursor는 null을 반환한다")
    void decode_blankCursor_returnsNull() {
        assertThat(cursorCodec.decode("  ")).isNull();
    }

    @Test
    @DisplayName("encode 후 decode하면 동일한 값을 반환한다")
    void encode_then_decode_roundtrip() {
        AnswerListCursor original = new AnswerListCursor(
                LocalDateTime.of(2026, 1, 15, 10, 30, 0),
                42L
        );

        String encoded = cursorCodec.encode(original);
        AnswerListCursor decoded = cursorCodec.decode(encoded);

        assertThat(decoded).isNotNull();
        assertThat(decoded.lastAnswerId()).isEqualTo(original.lastAnswerId());
        assertThat(decoded.lastCreatedAt()).isEqualTo(original.lastCreatedAt());
    }

    @Test
    @DisplayName("유효하지 않은 Base64 문자열은 예외를 발생시킨다")
    void decode_invalidBase64_throwsException() {
        assertThatThrownBy(() -> cursorCodec.decode("!!!invalid!!!"))
                .isInstanceOf(AnswerListInvalidInputException.class);
    }
}
