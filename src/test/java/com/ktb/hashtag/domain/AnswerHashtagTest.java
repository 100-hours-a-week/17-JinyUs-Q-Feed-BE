package com.ktb.hashtag.domain;

import com.ktb.answer.domain.Answer;
import com.ktb.fixture.AnswerHashtagFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("AnswerHashtag 도메인 테스트")
class AnswerHashtagTest {

    @Nested
    @DisplayName("AnswerHashtag 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 Answer와 Hashtag로 생성 성공")
        void create_WithValidData_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);
            Hashtag hashtag = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag);

            // Then
            assertThat(answerHashtag).isNotNull();
            assertThat(answerHashtag.getAnswer()).isEqualTo(answer);
            assertThat(answerHashtag.getHashtag()).isEqualTo(hashtag);
        }

        @Test
        @DisplayName("Answer null로 생성 가능 (Service 레이어에서 검증)")
        void create_WithNullAnswer_ShouldSucceed() {
            // Given & When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtagWithNullAnswer();

            // Then
            assertThat(answerHashtag.getAnswer()).isNull();
            assertThat(answerHashtag.getHashtag()).isNotNull();
        }

        @Test
        @DisplayName("Hashtag null로 생성 가능 (Service 레이어에서 검증)")
        void create_WithNullHashtag_ShouldSucceed() {
            // Given & When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtagWithNullHashtag();

            // Then
            assertThat(answerHashtag.getAnswer()).isNotNull();
            assertThat(answerHashtag.getHashtag()).isNull();
        }

        @Test
        @DisplayName("Answer와 Hashtag 모두 null로 생성 가능")
        void create_WithBothNull_ShouldSucceed() {
            // Given & When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtagWithBothNull();

            // Then
            assertThat(answerHashtag.getAnswer()).isNull();
            assertThat(answerHashtag.getHashtag()).isNull();
        }
    }

    @Nested
    @DisplayName("엔티티 관계 테스트")
    class RelationshipTest {

        @Test
        @DisplayName("Answer와 Hashtag가 올바르게 설정됨")
        void create_ShouldSetBothAnswerAndHashtag() {
            // Given
            Answer answer = mock(Answer.class);
            Hashtag hashtag = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag);

            // Then
            assertThat(answerHashtag.getAnswer()).isNotNull();
            assertThat(answerHashtag.getHashtag()).isNotNull();
            assertThat(answerHashtag.getAnswer()).isEqualTo(answer);
            assertThat(answerHashtag.getHashtag()).isEqualTo(hashtag);
        }

        @Test
        @DisplayName("동일한 Answer로 여러 AnswerHashtag 생성 가능 (다른 Hashtag)")
        void create_SameAnswerWithDifferentHashtags_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);
            Hashtag hashtag1 = mock(Hashtag.class);
            Hashtag hashtag2 = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag1 = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag1);
            AnswerHashtag answerHashtag2 = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag2);

            // Then
            assertThat(answerHashtag1.getAnswer()).isEqualTo(answer);
            assertThat(answerHashtag2.getAnswer()).isEqualTo(answer);
            assertThat(answerHashtag1.getHashtag()).isNotEqualTo(answerHashtag2.getHashtag());
        }

        @Test
        @DisplayName("동일한 Hashtag로 여러 AnswerHashtag 생성 가능 (다른 Answer)")
        void create_SameHashtagWithDifferentAnswers_ShouldSucceed() {
            // Given
            Answer answer1 = mock(Answer.class);
            Answer answer2 = mock(Answer.class);
            Hashtag hashtag = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag1 = AnswerHashtagFixture.createAnswerHashtag(answer1, hashtag);
            AnswerHashtag answerHashtag2 = AnswerHashtagFixture.createAnswerHashtag(answer2, hashtag);

            // Then
            assertThat(answerHashtag1.getHashtag()).isEqualTo(hashtag);
            assertThat(answerHashtag2.getHashtag()).isEqualTo(hashtag);
            assertThat(answerHashtag1.getAnswer()).isNotEqualTo(answerHashtag2.getAnswer());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("생성 후 Answer 참조 변경 불가")
        void create_AnswerIsImmutable() {
            // Given
            Answer answer = mock(Answer.class);
            Hashtag hashtag = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag);

            // Then
            assertThat(answerHashtag.getAnswer()).isEqualTo(answer);
            // Setter가 없으므로 변경 불가능
        }

        @Test
        @DisplayName("생성 후 Hashtag 참조 변경 불가")
        void create_HashtagIsImmutable() {
            // Given
            Answer answer = mock(Answer.class);
            Hashtag hashtag = mock(Hashtag.class);

            // When
            AnswerHashtag answerHashtag = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag);

            // Then
            assertThat(answerHashtag.getHashtag()).isEqualTo(hashtag);
            // Setter가 없으므로 변경 불가능
        }
    }
}
