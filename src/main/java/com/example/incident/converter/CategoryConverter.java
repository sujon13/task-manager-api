package com.example.incident.converter;

import com.example.incident.enums.IncidentCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoryConverter implements AttributeConverter<IncidentCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(IncidentCategory attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public IncidentCategory convertToEntityAttribute(Integer dbData) {
        return IncidentCategory.getByValue(dbData);
    }
}