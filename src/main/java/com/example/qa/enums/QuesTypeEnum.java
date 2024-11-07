package com.example.qa.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum QuesTypeEnum {
    MCQ(0),
    WRITTEN(1),
    SUB_QUESTION(2);

    private static final Map<Integer, QuesTypeEnum> mapByValue;

    static {
        mapByValue = Stream.of(QuesTypeEnum.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    QuesTypeEnum(int value) {
        this.value = value;
    }

    public static QuesTypeEnum getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}