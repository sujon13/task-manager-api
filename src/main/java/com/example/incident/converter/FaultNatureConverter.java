package com.example.incident.converter;

import com.example.incident.enums.FaultNature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class FaultNatureConverter implements AttributeConverter<FaultNature, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FaultNature attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FaultNature convertToEntityAttribute(Integer dbData) {
        return FaultNature.getByValue(dbData);
    }
}