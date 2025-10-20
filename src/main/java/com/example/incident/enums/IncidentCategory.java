package com.example.incident.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum IncidentCategory {
    RECON(0, "Recon"),
    HISTORIAN(1, "Historian"),
    REPORT_BUILDER(2, "Report Builder"),
    TREND(3, "Trend"),
    GATEWAY(4, "Gateway"),
    RTU(5, "RTU"),
    TELECOM_ABB(6, "Telecom-ABB"),
    TELECOM_GE(7, "Telecom-GE"),
    SCADA_DB_MODELING(8, "SCADA Database Modeling"),
    DISPLAY(9, "Display");


    private static final Map<Integer, IncidentCategory> mapByValue;

    static {
        mapByValue = Stream.of(IncidentCategory.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;
    private final String displayName;

    IncidentCategory(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static IncidentCategory getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}