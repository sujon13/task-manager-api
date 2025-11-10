package com.example.incident.converter;

import com.example.incident.enums.Division;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DivisionConverter implements AttributeConverter<Division, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Division attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Division convertToEntityAttribute(Integer dbData) {
        return Division.getByValue(dbData);
    }
}