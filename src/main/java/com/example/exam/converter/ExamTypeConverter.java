package com.example.exam.converter;

import com.example.exam.enums.ExamType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class ExamTypeConverter implements AttributeConverter<ExamType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ExamType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ExamType convertToEntityAttribute(Integer dbData) {
        return ExamType.getByValue(dbData);
    }
}
