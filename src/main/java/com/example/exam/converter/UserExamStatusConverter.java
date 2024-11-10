package com.example.exam.converter;

import com.example.exam.enums.UserExamStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class UserExamStatusConverter implements AttributeConverter<UserExamStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserExamStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UserExamStatus convertToEntityAttribute(Integer dbData) {
        return UserExamStatus.getByValue(dbData);
    }
}