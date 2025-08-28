package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum FaultNature {
    HARDWARE(0),
    SOFTWARE(1);

    private static final Map<Integer, FaultNature> mapByValue;

    static {
        mapByValue = Stream.of(FaultNature.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    FaultNature(int value) {
        this.value = value;
    }

    public static FaultNature getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}