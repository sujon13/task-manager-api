package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamCloneRequest {
    @NotNull
    private Integer parentExamId;

    @Builder.Default
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime = LocalDateTime.now();

    @NotNull
    private Integer allocatedTimeInMin;

    @NotNull
    private ExamType examType;

    private Integer totalQuestions;
    private Double totalMarks;
}