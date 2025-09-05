package com.example.exam.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class ExamQuesRequest {
    private Integer id;

    @NotNull
    private Integer questionId;

    @NotNull
    private Integer examId;

    private Integer marks = 1;

    private Integer ans;
}