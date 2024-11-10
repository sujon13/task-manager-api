package com.example.exam.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum UserExamStatus {
    ENTERED(0),
    EXITED(1);
    private static final Map<Integer, UserExamStatus> mapByValue;

    static {
        mapByValue = Stream.of(UserExamStatus.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    UserExamStatus(int value) {
        this.value = value;
    }

    public static UserExamStatus getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}