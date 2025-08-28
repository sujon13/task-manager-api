package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum IncidentStatus {
    REPORTED(0),
    IN_PROGRESS(1),
    IN_REVIEW(2),
    RESOLVED(3);

    private static final Map<Integer, IncidentStatus> mapByValue;

    static {
        mapByValue = Stream.of(IncidentStatus.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;

    IncidentStatus(int value) {
        this.value = value;
    }

    public static IncidentStatus getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}