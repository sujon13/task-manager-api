package com.example.qa.converter;

import com.example.qa.enums.QuestionVersion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class QuesVersionConverter implements AttributeConverter<QuestionVersion, Integer> {

    @Override
    public Integer convertToDatabaseColumn(QuestionVersion attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public QuestionVersion convertToEntityAttribute(Integer dbData) {
        return QuestionVersion.getByValue(dbData);
    }
}
