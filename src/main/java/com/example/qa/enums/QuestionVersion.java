package com.example.qa.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum QuestionVersion {
    English(0),
    Version(1);

    private static final Map<Integer, QuestionVersion> mapByValue;

    static {
        mapByValue = Stream.of(QuestionVersion.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    QuestionVersion(int value) {
        this.value = value;
    }

    public static QuestionVersion getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}