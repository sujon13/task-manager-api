package com.example.exam.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ExamStatus {
    NOT_SCHEDULED(0),
    NOT_STARTED(1),
    RUNNING(2),
    ENDED(3),
    CANCELLED(4);

    private static final Map<Integer, ExamStatus> mapByValue;

    static {
        mapByValue = Stream.of(ExamStatus.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    ExamStatus(int value) {
        this.value = value;
    }

    public static ExamStatus getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}