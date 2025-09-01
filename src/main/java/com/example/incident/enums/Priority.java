package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Priority {
    CRITICAL(1, "Critical"),
    HIGH(2, "High"),
    MEDIUM(3, "Medium"),
    LOW(4, "Low");

    private static final Map<Integer, Priority> mapByValue;

    static {
        mapByValue = Stream.of(Priority.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;
    private final String displayName;

    Priority(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static Priority getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}