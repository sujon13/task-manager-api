package com.example.incident.converter;

import com.example.incident.enums.Priority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PriorityConverter implements AttributeConverter<Priority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Priority attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Priority convertToEntityAttribute(Integer dbData) {
        return Priority.getByValue(dbData);
    }
}