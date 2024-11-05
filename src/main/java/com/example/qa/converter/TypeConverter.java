package com.example.qa.converter;

import com.example.qa.enums.TypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TypeConverter implements AttributeConverter<TypeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TypeEnum attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TypeEnum convertToEntityAttribute(Integer dbData) {
        return TypeEnum.getByValue(dbData);
    }
}
