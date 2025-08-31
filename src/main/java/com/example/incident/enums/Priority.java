package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Priority {
    CRITICAL(1),
    HIGH(2),
    MEDIUM(3),
    LOW(4);

    private static final Map<Integer, Priority> mapByValue;

    static {
        mapByValue = Stream.of(Priority.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public static Priority getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}