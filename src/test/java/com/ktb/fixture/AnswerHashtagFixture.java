package com.ktb.fixture;

import com.ktb.answer.domain.Answer;
import com.ktb.hashtag.domain.AnswerHashtag;
import com.ktb.hashtag.domain.Hashtag;

import static org.mockito.Mockito.mock;

public class AnswerHashtagFixture {

    public static AnswerHashtag createAnswerHashtag() {
        return AnswerHashtag.create(
                mock(Answer.class),
                mock(Hashtag.class),
            false
        );
    }

    public static AnswerHashtag createAnswerHashtag(Answer answer, Hashtag hashtag) {
        return AnswerHashtag.create(answer, hashtag, false);
    }

    public static AnswerHashtag createAnswerHashtagWithAnswer(Answer answer) {
        return AnswerHashtag.create(answer, mock(Hashtag.class), false);
    }

    public static AnswerHashtag createAnswerHashtagWithHashtag(Hashtag hashtag) {
        return AnswerHashtag.create(mock(Answer.class), hashtag, false);
    }

    public static AnswerHashtag[] createMultipleAnswerHashtags(Answer answer, Hashtag... hashtags) {
        return java.util.Arrays.stream(hashtags)
                .map(hashtag -> AnswerHashtag.create(answer, hashtag, false))
                .toArray(AnswerHashtag[]::new);
    }

    public static AnswerHashtag[] createMultipleAnswerHashtags(Hashtag hashtag, Answer... answers) {
        return java.util.Arrays.stream(answers)
                .map(answer -> AnswerHashtag.create(answer, hashtag, false))
                .toArray(AnswerHashtag[]::new);
    }

    public static AnswerHashtag[] createAnswerHashtagsWithNames(Answer answer, String... hashtagNames) {
        return java.util.Arrays.stream(hashtagNames)
                .map(name -> {
                    Hashtag hashtag = HashtagFixture.createHashtag(name);
                    return AnswerHashtag.create(answer, hashtag, false);
                })
                .toArray(AnswerHashtag[]::new);
    }

    public static AnswerHashtag createAnswerHashtagWithNullAnswer() {
        return AnswerHashtag.create(null, mock(Hashtag.class), false);
    }

    public static AnswerHashtag createAnswerHashtagWithNullHashtag() {
        return AnswerHashtag.create(mock(Answer.class), null, false);
    }

    public static AnswerHashtag createAnswerHashtagWithBothNull() {
        return AnswerHashtag.create(null, null, false);
    }
}
