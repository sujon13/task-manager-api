package com.example.exam.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ExamType {
    REAL(0),
    LIVE(1),
    PRACTICE(2);

    private static final Map<Integer, ExamType> mapByValue;

    static {
        mapByValue = Stream.of(ExamType.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    ExamType(int value) {
        this.value = value;
    }

    public static ExamType getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}