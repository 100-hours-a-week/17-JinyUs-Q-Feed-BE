package com.ktb.metric.domain;

import com.ktb.answer.domain.Answer;
import com.ktb.metric.exception.MetricInvalidRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DisplayName("AnswerMetric 도메인 테스트")
class AnswerMetricTest {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    @Nested
    @DisplayName("AnswerMetric 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 AnswerMetric 생성 성공")
        void create_WithValidData_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);
            int score = 85;

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, metric, score);

            // Then
            assertThat(answerMetric).isNotNull();
            assertThat(answerMetric.getAnswer()).isEqualTo(answer);
            assertThat(answerMetric.getMetric()).isEqualTo(metric);
            assertThat(answerMetric.getScore()).isEqualTo(score);
        }

        @Test
        @DisplayName("최소 점수(0)로 생성 성공")
        void create_WithMinScore_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, metric, MIN_SCORE);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(MIN_SCORE);
        }

        @Test
        @DisplayName("최대 점수(100)로 생성 성공")
        void create_WithMaxScore_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, metric, MAX_SCORE);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(MAX_SCORE);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 25, 50, 75, 99, 100})
        @DisplayName("0~100 범위 내 모든 점수로 생성 가능")
        void create_WithValidScores_ShouldSucceed(int score) {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, metric, score);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(score);
        }

        @Test
        @DisplayName("점수가 -1이면 MetricInvalidRangeException 발생")
        void create_WithNegativeScore_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, -1))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("점수가 101이면 MetricInvalidRangeException 발생")
        void create_WithScoreExceedingMax_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, 101))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-999999, -100, -10, -1})
        @DisplayName("음수 점수로 생성 시 MetricInvalidRangeException 발생")
        void create_WithNegativeScores_ShouldThrowException(int score) {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, score))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {101, 150, 200, 999999})
        @DisplayName("100 초과 점수로 생성 시 MetricInvalidRangeException 발생")
        void create_WithScoresExceedingMax_ShouldThrowException(int score) {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, score))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }
    }

    @Nested
    @DisplayName("점수 업데이트 테스트")
    class UpdateScoreTest {

        @Test
        @DisplayName("유효한 점수로 업데이트 성공")
        void updateScore_WithValidScore_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);
            int newScore = 90;

            // When
            answerMetric.updateScore(newScore);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(newScore);
        }

        @Test
        @DisplayName("점수를 0으로 업데이트 가능")
        void updateScore_ToMinScore_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When
            answerMetric.updateScore(MIN_SCORE);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(MIN_SCORE);
        }

        @Test
        @DisplayName("점수를 100으로 업데이트 가능")
        void updateScore_ToMaxScore_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When
            answerMetric.updateScore(MAX_SCORE);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(MAX_SCORE);
        }

        @Test
        @DisplayName("점수 증가 업데이트")
        void updateScore_Increasing_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(30);

            // When
            answerMetric.updateScore(70);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(70);
        }

        @Test
        @DisplayName("점수 감소 업데이트")
        void updateScore_Decreasing_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(90);

            // When
            answerMetric.updateScore(40);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(40);
        }

        @Test
        @DisplayName("점수를 -1로 업데이트 시 MetricInvalidRangeException 발생")
        void updateScore_WithNegativeScore_ShouldThrowException() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When & Then
            assertThatThrownBy(() -> answerMetric.updateScore(-1))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("점수를 101로 업데이트 시 MetricInvalidRangeException 발생")
        void updateScore_WithScoreExceedingMax_ShouldThrowException() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When & Then
            assertThatThrownBy(() -> answerMetric.updateScore(101))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("여러 번 점수 업데이트 가능")
        void updateScore_Multiple_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When
            answerMetric.updateScore(60);
            answerMetric.updateScore(70);
            answerMetric.updateScore(80);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(80);
        }

        @Test
        @DisplayName("동일한 점수로 업데이트 가능")
        void updateScore_WithSameScore_ShouldSucceed() {
            // Given
            AnswerMetric answerMetric = createAnswerMetric(50);

            // When
            answerMetric.updateScore(50);

            // Then
            assertThat(answerMetric.getScore()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("엔티티 관계 테스트")
    class RelationshipTest {

        @Test
        @DisplayName("Answer null로 생성 가능 (Service 레이어에서 검증)")
        void create_WithNullAnswer_ShouldSucceed() {
            // Given
            Metric metric = mock(Metric.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(null, metric, 50);

            // Then
            assertThat(answerMetric.getAnswer()).isNull();
        }

        @Test
        @DisplayName("Metric null로 생성 가능 (Service 레이어에서 검증)")
        void create_WithNullMetric_ShouldSucceed() {
            // Given
            Answer answer = mock(Answer.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, null, 50);

            // Then
            assertThat(answerMetric.getMetric()).isNull();
        }

        @Test
        @DisplayName("Answer와 Metric 모두 설정됨")
        void create_ShouldSetBothAnswerAndMetric() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When
            AnswerMetric answerMetric = AnswerMetric.create(answer, metric, 50);

            // Then
            assertThat(answerMetric.getAnswer()).isNotNull();
            assertThat(answerMetric.getMetric()).isNotNull();
            assertThat(answerMetric.getAnswer()).isEqualTo(answer);
            assertThat(answerMetric.getMetric()).isEqualTo(metric);
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("MIN_SCORE - 1은 실패")
        void create_WithBelowMinScore_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, MIN_SCORE - 1))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("MAX_SCORE + 1은 실패")
        void create_WithAboveMaxScore_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, MAX_SCORE + 1))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("Integer.MIN_VALUE는 실패")
        void create_WithIntegerMinValue_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, Integer.MIN_VALUE))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }

        @Test
        @DisplayName("Integer.MAX_VALUE는 실패")
        void create_WithIntegerMaxValue_ShouldThrowException() {
            // Given
            Answer answer = mock(Answer.class);
            Metric metric = mock(Metric.class);

            // When & Then
            assertThatThrownBy(() -> AnswerMetric.create(answer, metric, Integer.MAX_VALUE))
                    .isInstanceOf(MetricInvalidRangeException.class);
        }
    }

    // ==================== Fixture 메서드 ====================

    private AnswerMetric createAnswerMetric(int score) {
        return AnswerMetric.create(
                mock(Answer.class),
                mock(Metric.class),
                score
        );
    }
}
