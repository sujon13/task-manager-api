package com.example.exam.model;

import com.example.exam.enums.ExamType;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private Integer id;

    @NotNull
    private Integer examId;
    @NotNull
    private Integer questionId;
    @NotNull
    private ExamType examType;

    private Integer givenAns;
}