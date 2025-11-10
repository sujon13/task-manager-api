package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum Division {
    SCADA(1, "SCADA"),
    CNSTD(2, "CNSTD");

    private static final Map<Integer, Division> mapByValue;

    static {
        mapByValue = Stream.of(Division.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;
    private final String displayName;

    Division(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static Division getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}