package com.ktb.fixture;

import com.ktb.hashtag.domain.Hashtag;

public class HashtagFixture {

    private static final int MAX_NAME_LENGTH = 100;
    private static int counter = 0;

    public static Hashtag createHashtag() {
        return Hashtag.create("태그" + (++counter));
    }

    public static Hashtag createHashtag(String name) {
        return Hashtag.create(name);
    }

    public static Hashtag createHashtagWithDescription(String name, String description) {
        return Hashtag.createWithDescription(name, description);
    }

    public static Hashtag createHashtagWithMinLength() {
        return Hashtag.create("a");
    }

    public static Hashtag createHashtagWithMaxLength() {
        String maxName = "a".repeat(MAX_NAME_LENGTH);
        return Hashtag.create(maxName);
    }

    public static String createNameExceedingMaxLength() {
        return "a".repeat(MAX_NAME_LENGTH + 1);
    }

    public static Hashtag createHashtagWithUpperCase(String name) {
        return Hashtag.create(name.toUpperCase());
    }

    public static Hashtag createHashtagWithMixedCase(String name) {
        return Hashtag.create(name);
    }

    public static Hashtag createHashtagWithSpecialCharacters() {
        return Hashtag.create("c++");
    }

    public static Hashtag createHashtagWithHyphen() {
        return Hashtag.create("spring-boot");
    }

    public static Hashtag createHashtagWithUnderscore() {
        return Hashtag.create("snake_case");
    }

    public static Hashtag createHashtagWithNumbers() {
        return Hashtag.create("java8");
    }

    public static Hashtag createKoreanHashtag(String name) {
        return Hashtag.create(name);
    }

    public static Hashtag createEnglishHashtag(String name) {
        return Hashtag.create(name);
    }

    public static Hashtag createMixedLanguageHashtag() {
        return Hashtag.create("자바java");
    }

    public static Hashtag[] createMultipleHashtags(String... names) {
        return java.util.Arrays.stream(names)
                .map(Hashtag::create)
                .toArray(Hashtag[]::new);
    }

    public static Hashtag[] createUniqueHashtags(int count) {
        Hashtag[] hashtags = new Hashtag[count];
        for (int i = 0; i < count; i++) {
            hashtags[i] = createHashtag();
        }
        return hashtags;
    }

    public static void resetCounter() {
        counter = 0;
    }

    public static String createNameWithSpace() {
        return "spring boot";
    }

    public static String createNullName() {
        return null;
    }

    public static String createEmptyName() {
        return "";
    }

    public static String createBlankName() {
        return "   ";
    }
}
