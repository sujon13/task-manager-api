package com.example.incident.converter;

import com.example.incident.enums.IncidentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class IncidentStatusConverter implements AttributeConverter<IncidentStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(IncidentStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public IncidentStatus convertToEntityAttribute(Integer dbData) {
        return IncidentStatus.getByValue(dbData);
    }
}