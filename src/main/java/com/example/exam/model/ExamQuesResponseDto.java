package com.example.exam.model;

import com.example.qa.model.QuesResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExamQuesResponseDto {
    private Integer id;
    private int examId;
    private QuesResponse question;
}
