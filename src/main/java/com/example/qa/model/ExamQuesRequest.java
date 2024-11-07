package com.example.qa.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuesRequest {
    private Integer id;

    @NotNull
    private Integer questionId;

    @NotNull
    private Integer examId;
}