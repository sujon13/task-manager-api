package com.example.exam.converter;

import com.example.exam.enums.ExamStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class ExamStatusConverter implements AttributeConverter<ExamStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ExamStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ExamStatus convertToEntityAttribute(Integer dbData) {
        return ExamStatus.getByValue(dbData);
    }
}