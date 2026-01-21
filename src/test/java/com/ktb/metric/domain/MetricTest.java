package com.ktb.metric.domain;

import com.ktb.metric.exception.MetricNameInvalidLengthException;
import com.ktb.metric.exception.MetricRequiredNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Metric 도메인 테스트")
class MetricTest {

    private static final int MAX_NAME_LENGTH = 100;

    @Nested
    @DisplayName("Metric 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 이름과 설명으로 Metric 생성 성공")
        void create_WithValidNameAndDescription_ShouldSucceed() {
            // Given
            String name = "논리성";
            String description = "답변의 논리적 구조";

            // When
            Metric metric = Metric.create(name, description);

            // Then
            assertThat(metric).isNotNull();
            assertThat(metric.getName()).isEqualTo(name);
            assertThat(metric.getDescription()).isEqualTo(description);
        }

        @Test
        @DisplayName("설명 없이 Metric 생성 성공")
        void create_WithNullDescription_ShouldSucceed() {
            // Given
            String name = "논리성";

            // When
            Metric metric = Metric.create(name, null);

            // Then
            assertThat(metric.getName()).isEqualTo(name);
            assertThat(metric.getDescription()).isNull();
        }

        @Test
        @DisplayName("1자 이름으로 생성 성공")
        void create_WithOneCharacter_ShouldSucceed() {
            // Given
            String name = "A";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("100자 이름으로 생성 성공")
        void create_WithMaxLength_ShouldSucceed() {
            // Given
            String name = "A".repeat(MAX_NAME_LENGTH);

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).hasSize(MAX_NAME_LENGTH);
        }

        @Test
        @DisplayName("101자 이름으로 생성 시 MetricNameInvalidLengthException 발생")
        void create_WithExceedingMaxLength_ShouldThrowException() {
            // Given
            String name = "A".repeat(MAX_NAME_LENGTH + 1);

            // When & Then
            assertThatThrownBy(() -> Metric.create(name, "설명"))
                    .isInstanceOf(MetricNameInvalidLengthException.class);
        }

        @Test
        @DisplayName("null 이름으로 생성 시 MetricRequiredNameException 발생")
        void create_WithNull_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(() -> Metric.create(null, "설명"))
                    .isInstanceOf(MetricRequiredNameException.class);
        }

        @Test
        @DisplayName("빈 문자열 이름으로 생성 시 MetricRequiredNameException 발생")
        void create_WithEmptyString_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(() -> Metric.create("", "설명"))
                    .isInstanceOf(MetricRequiredNameException.class);
        }

        @Test
        @DisplayName("공백만 있는 이름으로 생성 시 MetricRequiredNameException 발생")
        void create_WithOnlySpaces_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(() -> Metric.create("   ", "설명"))
                    .isInstanceOf(MetricRequiredNameException.class);
        }
    }

    @Nested
    @DisplayName("특수 문자 및 공백 테스트")
    class SpecialCharacterTest {

        @Test
        @DisplayName("공백이 포함된 이름 생성 가능")
        void create_WithSpaces_ShouldSucceed() {
            // Given
            String name = "논리성 평가";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("특수문자가 포함된 이름 생성 가능")
        void create_WithSpecialCharacters_ShouldSucceed() {
            // Given
            String name = "논리성(Logic)";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("숫자가 포함된 이름 생성 가능")
        void create_WithNumbers_ShouldSucceed() {
            // Given
            String name = "평가지표1";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("영어와 한글이 혼합된 이름 생성 가능")
        void create_WithKoreanAndEnglish_ShouldSucceed() {
            // Given
            String name = "논리성Logic";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("설명 업데이트 테스트")
    class UpdateDescriptionTest {

        @Test
        @DisplayName("설명 업데이트 성공")
        void updateDescription_WithValidDescription_ShouldSucceed() {
            // Given
            Metric metric = Metric.create("논리성", "기존 설명");
            String newDescription = "답변의 논리적 구조와 일관성";

            // When
            metric.updateDescription(newDescription);

            // Then
            assertThat(metric.getDescription()).isEqualTo(newDescription);
        }

        @Test
        @DisplayName("설명을 null로 업데이트 가능")
        void updateDescription_WithNull_ShouldSucceed() {
            // Given
            Metric metric = Metric.create("논리성", "기존 설명");

            // When
            metric.updateDescription(null);

            // Then
            assertThat(metric.getDescription()).isNull();
        }

        @Test
        @DisplayName("설명을 빈 문자열로 업데이트 가능")
        void updateDescription_WithEmptyString_ShouldSucceed() {
            // Given
            Metric metric = Metric.create("논리성", "기존 설명");

            // When
            metric.updateDescription("");

            // Then
            assertThat(metric.getDescription()).isEmpty();
        }

        @Test
        @DisplayName("255자 설명 업데이트 가능")
        void updateDescription_WithMaxLength_ShouldSucceed() {
            // Given
            Metric metric = Metric.create("논리성", "기존 설명");
            String longDescription = "A".repeat(255);

            // When
            metric.updateDescription(longDescription);

            // Then
            assertThat(metric.getDescription()).hasSize(255);
        }

        @Test
        @DisplayName("기존 설명 덮어쓰기 가능")
        void updateDescription_ShouldOverwriteExisting() {
            // Given
            Metric metric = Metric.create("논리성", "첫 번째 설명");

            // When
            metric.updateDescription("두 번째 설명");
            metric.updateDescription("세 번째 설명");

            // Then
            assertThat(metric.getDescription()).isEqualTo("세 번째 설명");
        }
    }

    @Nested
    @DisplayName("이름 trim 테스트")
    class NameTrimTest {

        @Test
        @DisplayName("이름 앞의 공백은 제거되지 않음")
        void create_WithLeadingSpaces_ShouldNotTrim() {
            // Given
            String name = "  논리성";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("이름 뒤의 공백은 제거되지 않음")
        void create_WithTrailingSpaces_ShouldNotTrim() {
            // Given
            String name = "논리성  ";

            // When
            Metric metric = Metric.create(name, "설명");

            // Then
            assertThat(metric.getName()).isEqualTo(name);
        }
    }
}
