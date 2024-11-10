package com.example.exam.model;

import com.example.exam.enums.ExamType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequest {
    private Integer examId;
    private ExamType examType;
    private String examinee;
    private Double marksObtained;
    private Integer position;
}
