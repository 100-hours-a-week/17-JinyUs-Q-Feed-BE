# Fixture 클래스 사용 가이드

> 테스트 데이터 생성을 위한 재사용 가능한 Fixture 패키지

---

## 📋 개요

이 패키지는 테스트에서 반복적으로 사용되는 엔티티 생성 로직을 중앙화하여 관리합니다.
**Fixture 패턴**을 통해 테스트 코드의 가독성을 높이고 유지보수를 용이하게 합니다.

---

## 🎯 Fixture 패턴의 이점

### ✅ 장점
1. **재사용성**: 동일한 테스트 데이터를 여러 테스트에서 일관되게 사용
2. **가독성**: Given 절이 간결해지고 테스트 의도가 명확해짐
3. **유지보수성**: 엔티티 구조 변경 시 Fixture만 수정하면 됨
4. **일관성**: 프로젝트 전체에서 동일한 방식으로 테스트 데이터 생성

### ❌ Fixture 없이 작성한 경우
```java
@Test
void test() {
    // Given - 복잡하고 반복적인 생성 로직
    UserAccount account = mock(UserAccount.class);
    when(account.getId()).thenReturn(1L);
    Question question = mock(Question.class);
    Answer answer = Answer.create(
        question,
        account,
        "테스트 답변 내용입니다.",
        AnswerType.PRACTICE_INTERVIEW
    );
    answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
    answer.setAiFeedback("AI 피드백");

    // When & Then...
}
```

### ✅ Fixture를 사용한 경우
```java
@Test
void test() {
    // Given - 간결하고 명확한 의도 표현
    Answer answer = AnswerFixture.createCompletedAnswer();

    // When & Then...
}
```

---

## 📦 Fixture 클래스 목록

| Fixture 클래스 | 설명 | 위치 |
|--------------|------|------|
| **AnswerFixture** | Answer 엔티티 생성 | `com.ktb.fixture.AnswerFixture` |
| **HashtagFixture** | Hashtag 엔티티 생성 | `com.ktb.fixture.HashtagFixture` |
| **MetricFixture** | Metric, AnswerMetric 생성 | `com.ktb.fixture.MetricFixture` |
| **AnswerHashtagFixture** | AnswerHashtag 연관 생성 | `com.ktb.fixture.AnswerHashtagFixture` |

---

## 🔧 AnswerFixture 사용법

### 기본 생성
```java
// 기본 Answer 생성
Answer answer = AnswerFixture.createAnswer();

// 특정 content를 가진 Answer
Answer answer = AnswerFixture.createAnswerWithContent("특정 내용");

// 특정 AnswerType을 가진 Answer
Answer answer = AnswerFixture.createAnswerWithType(AnswerType.REAL_INTERVIEW);
```

### 특정 상태의 Answer 생성
```java
// SUBMITTED 상태 (기본)
Answer submitted = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);

// AI_FEEDBACK_PROCESSING 상태
Answer processing = AnswerFixture.createAnswerWithStatus(AnswerStatus.AI_FEEDBACK_PROCESSING);

// COMPLETED 상태 (AI 피드백 포함)
Answer completed = AnswerFixture.createCompletedAnswer();

// FAILED 상태
Answer failed = AnswerFixture.createFailedAnswer();

// FAILED_RETRYABLE 상태
Answer retryable = AnswerFixture.createRetryableFailedAnswer();
```

### UserAccount와 함께 생성
```java
// Mock UserAccount와 함께
UserAccount account = mock(UserAccount.class);
Answer answer = AnswerFixture.createAnswerWithAccount(account);

// 특정 ID를 가진 Account와 함께
Answer answer = AnswerFixture.createAnswerWithAccountId(1L);
```

### 경계값 테스트용
```java
// 최대 길이 content
Answer maxContent = AnswerFixture.createAnswerWithMaxContent();

// 최대 길이 초과 content (예외 발생용)
String exceedingContent = AnswerFixture.createContentExceedingMaxLength();
```

---

## 🔧 HashtagFixture 사용법

### 기본 생성
```java
// 고유한 이름의 Hashtag (자동 증가)
Hashtag hashtag = HashtagFixture.createHashtag();

// 특정 이름의 Hashtag
Hashtag java = HashtagFixture.createHashtag("java");

// 설명과 함께
Hashtag hashtag = HashtagFixture.createHashtagWithDescription("java", "자바 프로그래밍");
```

### 경계값 및 예외 테스트용
```java
// 1자 이름
Hashtag min = HashtagFixture.createHashtagWithMinLength();

// 100자 이름
Hashtag max = HashtagFixture.createHashtagWithMaxLength();

// 101자 이름 (예외 발생용)
String exceeding = HashtagFixture.createNameExceedingMaxLength();

// 공백 포함 (예외 발생용)
String withSpace = HashtagFixture.createNameWithSpace();

// null, 빈 문자열 (예외 발생용)
String nullName = HashtagFixture.createNullName();
String empty = HashtagFixture.createEmptyName();
String blank = HashtagFixture.createBlankName();
```

### 특수 케이스
```java
// 특수문자 포함
Hashtag cpp = HashtagFixture.createHashtagWithSpecialCharacters(); // "c++"

// 하이픈 포함
Hashtag springBoot = HashtagFixture.createHashtagWithHyphen(); // "spring-boot"

// 언더스코어 포함
Hashtag snake = HashtagFixture.createHashtagWithUnderscore(); // "snake_case"

// 숫자 포함
Hashtag java8 = HashtagFixture.createHashtagWithNumbers(); // "java8"

// 혼합 언어
Hashtag mixed = HashtagFixture.createMixedLanguageHashtag(); // "자바java"
```

### 여러 개 생성
```java
// 특정 이름들로 여러 개 생성
Hashtag[] hashtags = HashtagFixture.createMultipleHashtags("java", "spring", "jpa");

// 고유한 이름으로 n개 생성
Hashtag[] uniqueHashtags = HashtagFixture.createUniqueHashtags(5);
```

---

## 🔧 MetricFixture 사용법

### Metric 생성
```java
// 기본 Metric (고유한 이름)
Metric metric = MetricFixture.createMetric();

// 특정 이름의 Metric
Metric logic = MetricFixture.createMetric("논리성");

// 이름과 설명 함께
Metric metric = MetricFixture.createMetric("논리성", "답변의 논리적 구조");

// 설명 없이
Metric metric = MetricFixture.createMetricWithoutDescription("논리성");
```

### 자주 사용되는 평가 지표
```java
Metric logic = MetricFixture.createLogicMetric();         // 논리성
Metric clarity = MetricFixture.createClarityMetric();     // 명확성
Metric conciseness = MetricFixture.createConcisenessMetric(); // 간결성
Metric completeness = MetricFixture.createCompletenessMetric(); // 완성도
```

### AnswerMetric 생성
```java
// 기본 AnswerMetric (점수 50)
AnswerMetric am = MetricFixture.createAnswerMetric();

// 특정 점수
AnswerMetric am80 = MetricFixture.createAnswerMetric(80);

// 특정 Answer, Metric과 함께
AnswerMetric am = MetricFixture.createAnswerMetric(answer, metric, 85);

// 최소/최대 점수
AnswerMetric min = MetricFixture.createAnswerMetricWithMinScore(); // 0
AnswerMetric max = MetricFixture.createAnswerMetricWithMaxScore(); // 100
```

### 점수 범위별 생성
```java
// 높은 점수 (85)
AnswerMetric high = MetricFixture.createHighScoreAnswerMetric();

// 중간 점수 (50)
AnswerMetric medium = MetricFixture.createMediumScoreAnswerMetric();

// 낮은 점수 (15)
AnswerMetric low = MetricFixture.createLowScoreAnswerMetric();
```

### 예외 테스트용
```java
// 범위 초과 점수
int belowMin = MetricFixture.createScoreBelowMin(); // -1
int aboveMax = MetricFixture.createScoreAboveMax(); // 101
```

---

## 🔧 AnswerHashtagFixture 사용법

### 기본 생성
```java
// Mock Answer, Mock Hashtag와 함께
AnswerHashtag ah = AnswerHashtagFixture.createAnswerHashtag();

// 특정 Answer와 Hashtag
AnswerHashtag ah = AnswerHashtagFixture.createAnswerHashtag(answer, hashtag);

// Answer만 지정
AnswerHashtag ah = AnswerHashtagFixture.createAnswerHashtagWithAnswer(answer);

// Hashtag만 지정
AnswerHashtag ah = AnswerHashtagFixture.createAnswerHashtagWithHashtag(hashtag);
```

### 여러 개 생성
```java
// 하나의 Answer에 여러 Hashtag 연결
Hashtag[] hashtags = {tag1, tag2, tag3};
AnswerHashtag[] ahs = AnswerHashtagFixture.createMultipleAnswerHashtags(answer, hashtags);

// 하나의 Hashtag에 여러 Answer 연결
Answer[] answers = {answer1, answer2};
AnswerHashtag[] ahs = AnswerHashtagFixture.createMultipleAnswerHashtags(hashtag, answers);

// Answer와 태그 이름 배열로 생성
AnswerHashtag[] ahs = AnswerHashtagFixture.createAnswerHashtagsWithNames(
    answer,
    "java", "spring", "jpa"
);
```

### null 테스트용
```java
AnswerHashtag nullAnswer = AnswerHashtagFixture.createAnswerHashtagWithNullAnswer();
AnswerHashtag nullHashtag = AnswerHashtagFixture.createAnswerHashtagWithNullHashtag();
AnswerHashtag bothNull = AnswerHashtagFixture.createAnswerHashtagWithBothNull();
```

---

## 📝 테스트 작성 예시

### Before (Fixture 없이)
```java
@Test
void test() {
    // Given - 복잡하고 반복적
    UserAccount account = mock(UserAccount.class);
    when(account.getId()).thenReturn(1L);
    Answer answer = Answer.create(
        mock(Question.class),
        account,
        "테스트 답변",
        AnswerType.PRACTICE_INTERVIEW
    );

    Metric logic = Metric.create("논리성", "설명");
    AnswerMetric am = AnswerMetric.create(answer, logic, 85);

    // When
    am.updateScore(90);

    // Then
    assertThat(am.getScore()).isEqualTo(90);
}
```

### After (Fixture 사용)
```java
@Test
void test() {
    // Given - 간결하고 명확
    Answer answer = AnswerFixture.createAnswer();
    Metric logic = MetricFixture.createLogicMetric();
    AnswerMetric am = MetricFixture.createAnswerMetric(answer, logic, 85);

    // When
    am.updateScore(90);

    // Then
    assertThat(am.getScore()).isEqualTo(90);
}
```

---

## 🎨 Best Practices

### 1. 의미 있는 메서드 선택
```java
// ❌ 나쁜 예
Answer answer = AnswerFixture.createAnswer();
answer.transitionTo(AnswerStatus.AI_FEEDBACK_PROCESSING);
answer.setAiFeedback("피드백");

// ✅ 좋은 예
Answer answer = AnswerFixture.createCompletedAnswer();
```

### 2. 테스트 의도에 맞는 Fixture 사용
```java
// 상태 전이 테스트
@Test
void transitionTest() {
    Answer submitted = AnswerFixture.createAnswerWithStatus(AnswerStatus.SUBMITTED);
    // ...
}

// 소유권 테스트
@Test
void ownershipTest() {
    Answer answer = AnswerFixture.createAnswerWithAccountId(1L);
    // ...
}

// 경계값 테스트
@Test
void boundaryTest() {
    Answer maxContent = AnswerFixture.createAnswerWithMaxContent();
    // ...
}
```

### 3. 불필요한 설정 피하기
```java
// ❌ 나쁜 예 - 불필요한 Mock 설정
UserAccount account = mock(UserAccount.class);
when(account.getId()).thenReturn(1L);
when(account.getNickname()).thenReturn("테스터");
when(account.getEmail()).thenReturn("test@test.com");
Answer answer = AnswerFixture.createAnswerWithAccount(account);

// ✅ 좋은 예 - 필요한 것만 설정
Answer answer = AnswerFixture.createAnswerWithAccountId(1L);
```

### 4. 테스트 간 독립성 보장
```java
@BeforeEach
void setUp() {
    // Counter 초기화로 테스트 간 독립성 보장
    HashtagFixture.resetCounter();
    MetricFixture.resetCounter();
}
```

---

## 🔄 Fixture 확장 가이드

새로운 엔티티에 대한 Fixture를 추가할 때는 다음 패턴을 따르세요:

```java
public class NewEntityFixture {

    // 1. 기본 생성 메서드
    public static NewEntity createEntity() { }

    // 2. 특정 필드를 가진 생성 메서드
    public static NewEntity createEntityWithField(Type field) { }

    // 3. 경계값 테스트용 메서드
    public static NewEntity createEntityWithMaxValue() { }
    public static String createValueExceedingMax() { }

    // 4. 예외 테스트용 메서드
    public static String createNullValue() { }
    public static String createEmptyValue() { }

    // 5. 여러 개 생성 메서드
    public static NewEntity[] createMultipleEntities(int count) { }

    // 6. Counter 초기화
    public static void resetCounter() { }
}
```

---

## 📊 Fixture 사용 통계

현재 프로젝트의 Fixture 사용 현황:

| 도메인 | Fixture 메서드 수 | 주요 기능 |
|-------|----------------|----------|
| **Answer** | 14개 | 상태 전이, 소유권, 경계값 |
| **Hashtag** | 18개 | 정규화, 특수문자, 다국어 |
| **Metric** | 24개 | 점수 범위, 평가 지표, 연관 관계 |
| **AnswerHashtag** | 8개 | 다대다 관계, null 처리 |

**총 64개의 재사용 가능한 Fixture 메서드**

---

## 🎓 참고 자료

- [Test Data Builder 패턴](https://martinfowler.com/bliki/ObjectMother.html)
- [Fixture 패턴 설명](https://xunitpatterns.com/Test%20Fixture.html)
- 프로젝트 규칙: `/backend/CLAUDE.md` (테스트 Fixture/Factory 패턴)

---

**Last Updated**: 2026-01-21
**Maintainer**: QFeed Backend Team
