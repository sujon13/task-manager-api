package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum IncidentStatus {
    REPORTED(0, "Reported"),
    IN_PROGRESS(1, "In Progress"),
    IN_REVIEW(2, "Under Observation"),
    RESOLVED(3, "Resolved");

    private static final Map<Integer, IncidentStatus> mapByValue;

    static {
        mapByValue = Stream.of(IncidentStatus.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;
    private final String displayName;

    IncidentStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static IncidentStatus getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}