package com.example.qa.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum TypeEnum {
    QUESTION(0),
    COMMENT(1);

    private static final Map<Integer, TypeEnum> mapByValue;

    static {
        mapByValue = Stream.of(TypeEnum.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    TypeEnum(int value) {
        this.value = value;
    }

    public static TypeEnum getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}