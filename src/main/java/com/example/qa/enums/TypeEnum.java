package com.example.qa.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TypeEnum {
    Question(0),
    Comment(1);

    private static final Map<Integer, TypeEnum> mapByValue;

    static {
        mapByValue = Stream.of(TypeEnum.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    TypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TypeEnum getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}