package com.example.qa.converter;

import com.example.qa.enums.QuesTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class QuesTypeConverter implements AttributeConverter<QuesTypeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(QuesTypeEnum attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public QuesTypeEnum convertToEntityAttribute(Integer dbData) {
        return QuesTypeEnum.getByValue(dbData);
    }
}